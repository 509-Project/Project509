'use strict';

var usernamePage = document.querySelector('#username-page');
var chatPage = document.querySelector('#chat-page');
var usernameForm = document.querySelector('#usernameForm');
var messageForm = document.querySelector('#messageForm');
var messageInput = document.querySelector('#message');
var messageArea = document.querySelector('#messageArea');
var connectingElement = document.querySelector('.connecting');

var stompClient = null;

var chatRoomId = null;

var colors = [
    '#2196F3', '#32c787', '#00BCD4', '#ff5652',
    '#ffc107', '#ff85af', '#FF9800', '#39bbb0'
];

function connect(event) {

    chatRoomId = document.querySelector('#chatRoomId').value.trim();

    const jwtToken = '인증된jwt토큰';
    localStorage.setItem('jwtToken', jwtToken);

    if(chatRoomId) {
        usernamePage.classList.add('hidden');
        chatPage.classList.remove('hidden');

        var socket = new SockJS(`/ws?token=${jwtToken}`);
        stompClient = Stomp.over(socket);

        // Heartbeat 설정
        stompClient.heartbeat.outgoing = 10000; // 클라이언트 -> 서버, 10초 간격으로 heart-beat 전송
        stompClient.heartbeat.incoming = 10000; // 서버 -> 클라이언트, 10초 간격으로 heart-beat 수신

        stompClient.connect({chatRoomId: chatRoomId}, onConnected, onError);
    }
    event.preventDefault();
}


function onConnected() {
    // Subscribe to the Public Topic
    stompClient.subscribe('/topic/' + chatRoomId, onMessageReceived);

    // 서버에 이전 메시지 요청
    fetch('/chat/history/' + chatRoomId)
        .then(response => response.json())
        .then(messages => {
            messages.forEach(message => {
                onMessageReceived({ body: JSON.stringify(message) });
            });
        });

    connectingElement.classList.add('hidden');
}


function onError(error) {
    connectingElement.textContent = 'Could not connect to WebSocket server. Please refresh this page to try again!';
    connectingElement.style.color = 'red';

    // 재연결 로직
    setTimeout(() => {
        console.log('Attempting to reconnect...');
        connect(); // 재연결 시도
    }, 5000); // 5초 후 재연결 시도
}

function sendMessage(event) {
    var messageContent = messageInput.value.trim();
    if(messageContent && stompClient) {
        var chatMessage = {
            sender: '',
            content: messageInput.value,
            type: 'CHAT'
        };
        stompClient.send("/app/chat.sendMessage/" + chatRoomId, {}, JSON.stringify(chatMessage));
        messageInput.value = '';
    }
    event.preventDefault();
}


function onMessageReceived(payload) {
    var message = JSON.parse(payload.body);

    var messageElement = document.createElement('li');

    if(message.type === 'JOIN') {
        messageElement.classList.add('event-message');
        message.content = message.sender + ' joined!';
    } else if (message.type === 'LEAVE') {
        messageElement.classList.add('event-message');
        message.content = message.sender + ' left!';
    } else {
        messageElement.classList.add('chat-message');

        var avatarElement = document.createElement('i');
        var avatarText = document.createTextNode(message.sender[0]);
        avatarElement.appendChild(avatarText);
        avatarElement.style['background-color'] = getAvatarColor(message.sender);

        messageElement.appendChild(avatarElement);

        var usernameElement = document.createElement('span');
        var usernameText = document.createTextNode(message.sender);
        usernameElement.appendChild(usernameText);
        messageElement.appendChild(usernameElement);
    }

    var textElement = document.createElement('p');
    var messageText = document.createTextNode(message.content);
    textElement.appendChild(messageText);

    messageElement.appendChild(textElement);

    messageArea.appendChild(messageElement);
    messageArea.scrollTop = messageArea.scrollHeight;
}


function getAvatarColor(messageSender) {
    var hash = 0;
    for (var i = 0; i < messageSender.length; i++) {
        hash = 31 * hash + messageSender.charCodeAt(i);
    }
    var index = Math.abs(hash % colors.length);
    return colors[index];
}

// HTML 이벤트 리스너에서는 event.preventDefault()가 필요함
usernameForm.addEventListener('submit', function(event) {
    event.preventDefault(); // 기본 제출 동작 방지
    connect();
}, true);
messageForm.addEventListener('submit', sendMessage, true)