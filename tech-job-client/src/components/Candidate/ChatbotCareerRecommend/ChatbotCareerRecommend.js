import React, { useEffect, useRef, useState } from "react";
import { Button, Card, Form, InputGroup, Spinner } from "react-bootstrap";
import "./ChatbotCareerRecommend.css";
import "../../styles/common.css";
import { authApis, endpoints } from "../../../configs/Apis";
import cookies from "react-cookies";

const ChatbotCareerRecommend = () => {
  const [isOpen, setIsOpen] = useState(false);
  const [disableSend, setDisableSend] = useState(false);
  const [chatSessionId, setChatSessionId] = useState(null);
  const [typing, setTyping] = useState(false);
  const [messages, setMessages] = useState([
    {
      text: "Xin chào, tôi là trợ lý ảo. Tôi có thể giúp gì cho bạn?",
      sender: "ASSISTANT",
    },
  ]);
  const [messageInput, setMessageInput] = useState("");
  const messageBottomRef = useRef(null);

  const toggleChat = () => setIsOpen(!isOpen);

  const handleSend = (e) => {
    e.preventDefault();
    if (messageInput.trim() === "") return;

    addMessage({ text: messageInput, sender: "USER" });
    setMessageInput("");
    setDisableSend(true);
    setTyping(true);

    sendMessageToBot(messageInput);
  };

  const addMessage = (message) => {
    setMessages((prevMessages) => [...prevMessages, message]);
  };

  const sendMessageToBot = async (message) => {
    try {
      const token = cookies.load("token");
      const questionGenerated = await authApis(token).post(
        endpoints.search_career,
        {
          chatSessionId: chatSessionId == null ? 0 : chatSessionId,
          content: message,
        },
      );

      if (questionGenerated.status === 200) {
        setChatSessionId(questionGenerated.data.chatSessionId);
        addMessage(questionGenerated.data);
      }
    } catch (err) {
      console.error("Error sending message to bot:", err);
    } finally {
      setDisableSend(false);
      setTyping(false);
    }
  };

  useEffect(() => {
    messageBottomRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [messages]);

  return (
    <div className='chatbot-container'>
      {isOpen && (
        <Card className='chatbot-window'>
          <Card.Header className='d-flex justify-content-between align-items-center text-white primary-background-color'>
            <span className='fw-bold'>Tư vấn nghề nghiệp</span>
            <Button
              variant='close'
              onClick={toggleChat}
              className='btn-close-white'
              aria-label='Đóng'
            />
          </Card.Header>
          <Card.Body className='chatbot-body'>
            {messages.map((msg, index) => (
              <div
                key={index}
                className={`mb-2 p-2 rounded ${
                  msg.sender === "USER"
                    ? "primary-background-color text-white text-end ms-auto chat-message"
                    : "bg-light text-start me-auto text-dark chat-message"
                }`}
                dangerouslySetInnerHTML={{ __html: msg.text }}
              ></div>
            ))}

            {typing && (
              <div className='d-flex align-items-center'>
                {Array.from({ length: 3 }).map((_, i) => (
                  <Spinner
                    key={i}
                    animation='grow'
                    size='sm'
                    className='ms-1'
                  />
                ))}
              </div>
            )}
            <div ref={messageBottomRef}></div>
          </Card.Body>
          <Card.Footer className='bg-white'>
            <Form onSubmit={handleSend}>
              <InputGroup>
                <Form.Control
                  type='text'
                  placeholder='Nhập tin nhắn...'
                  value={messageInput}
                  onChange={(e) => setMessageInput(e.target.value)}
                  className='message-input'
                />
                <Button
                  type='submit'
                  className='custom-button'
                  disabled={disableSend}
                >
                  <i class='bi bi-send'></i>
                </Button>
              </InputGroup>
            </Form>
          </Card.Footer>
        </Card>
      )}

      {/* Nút bong bóng chat hiển thị khi isOpen = false */}
      {!isOpen && (
        <Button
          className='rounded-circle shadow d-flex align-items-center justify-content-center chatbot-button custom-button primary-color'
          onClick={toggleChat}
        >
          <i class='bi bi-robot chatbot-icon'></i>
        </Button>
      )}
    </div>
  );
};

export default ChatbotCareerRecommend;
