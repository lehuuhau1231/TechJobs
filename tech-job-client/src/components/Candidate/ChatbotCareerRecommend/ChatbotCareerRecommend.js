import React, { useEffect, useRef, useState } from "react";
import { Button, Card, Form, InputGroup, Spinner } from "react-bootstrap";
import "./ChatbotCareerRecommend.css";
import "../../styles/common.css";
import { authApis, endpoints } from "../../../configs/Apis";
import cookies from "react-cookies";
import ReactMarkdown from "react-markdown";
import { MessageCircle } from "lucide-react";

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
      } else if (questionGenerated.status === 429) {
        addMessage({
          text: "Xin lỗi, ban đang gửi quá nhiều yêu cầu trong 1 phút. Vui lòng thử lại sau.",
          sender: "ASSISTANT",
        });
      }
    } catch (err) {
      if (err.status === 429) {
        addMessage({
          text: "Xin lỗi, ban đang gửi quá nhiều yêu cầu trong 1 phút. Vui lòng thử lại sau.",
          sender: "ASSISTANT",
        });
      }
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
        <div className='chatbot-window'>
          <div className='chatbot-header'>
            <div className='d-flex align-items-center gap-2'>
              <MessageCircle size={20} />
              <span className='fw-bold' style={{ fontSize: "0.95rem" }}>
                Trợ lý nghề nghiệp
              </span>
            </div>
            <Button
              variant='close'
              onClick={toggleChat}
              className='btn-close-white'
              aria-label='Đóng'
              style={{ boxShadow: "none" }}
            />
          </div>
          <div className='chatbot-body'>
            {messages.map((msg, index) => (
              <div
                key={index}
                className={`chat-message ${
                  msg.sender === "USER" ? "message-user" : "message-assistant"
                }`}
              >
                <ReactMarkdown>{msg.text}</ReactMarkdown>
              </div>
            ))}

            {typing && (
              <div className='d-flex gap-2 p-2 align-items-center'>
                <div
                  className='spinner-grow spinner-grow-sm text-primary'
                  role='status'
                  style={{ width: "8px", height: "8px" }}
                ></div>
                <div
                  className='spinner-grow spinner-grow-sm text-primary'
                  role='status'
                  style={{
                    width: "8px",
                    height: "8px",
                    animationDelay: "0.2s",
                  }}
                ></div>
                <div
                  className='spinner-grow spinner-grow-sm text-primary'
                  role='status'
                  style={{
                    width: "8px",
                    height: "8px",
                    animationDelay: "0.4s",
                  }}
                ></div>
              </div>
            )}
            <div ref={messageBottomRef}></div>
          </div>
          <div className='chatbot-footer'>
            <Form onSubmit={handleSend}>
              <InputGroup>
                <Form.Control
                  type='text'
                  placeholder='Hỏi tôi về công việc...'
                  value={messageInput}
                  onChange={(e) => setMessageInput(e.target.value)}
                  className='message-input-custom'
                />
                <Button
                  type='submit'
                  variant='link'
                  className='text-primary p-0 ms-2'
                  disabled={disableSend}
                  style={{ boxShadow: "none" }}
                >
                  <i
                    className='bi bi-send-fill'
                    style={{ fontSize: "1.5rem" }}
                  ></i>
                </Button>
              </InputGroup>
            </Form>
          </div>
        </div>
      )}

      {/* Toggle Button */}
      {!isOpen && (
        <button className='chatbot-toggle-button' onClick={toggleChat}>
          <i className='bi bi-robot' style={{ fontSize: "1.8rem" }}></i>
        </button>
      )}
    </div>
  );
};

export default ChatbotCareerRecommend;
