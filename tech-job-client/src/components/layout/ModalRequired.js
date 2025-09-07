import { useState } from "react";
import { Button, Modal } from "react-bootstrap";
import { useNavigate } from "react-router-dom";

const ModalRequired = ({
  message,
  redirectPath,
  messageRedirect,
  showModal,
  setShowModal,
}) => {
  const navigate = useNavigate();

  return (
    <Modal show={showModal} onHide={() => setShowModal(false)} centered>
      <Modal.Header closeButton>
        <Modal.Title>Thông báo</Modal.Title>
      </Modal.Header>
      <Modal.Body>
        <p>{message}</p>
      </Modal.Body>
      <Modal.Footer>
        <Button variant='secondary' onClick={() => setShowModal(false)}>
          Đóng
        </Button>
        <Button className='button' onClick={() => navigate(redirectPath)}>
          {messageRedirect}
        </Button>
      </Modal.Footer>
    </Modal>
  );
};

export default ModalRequired;
