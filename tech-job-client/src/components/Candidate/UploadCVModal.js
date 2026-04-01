import { useState } from "react";
import { Modal } from "react-bootstrap";
import UploadCVInterface from "./UploadCVInterface";

const UploadCVModal = ({ showCVModal, setShowCVModal, setMessage }) => {
  const handleClose = () => setShowCVModal(false);
  console.log("showCVModal in UploadCVModal: ", showCVModal);
  return (
    <Modal show={showCVModal} onHide={handleClose}>
      <Modal.Header closeButton>
        <Modal.Title>Thay thế CV</Modal.Title>
      </Modal.Header>
      <Modal.Body>
        <UploadCVInterface
          setMessage={setMessage}
          setShowModal={setShowCVModal}
        />
      </Modal.Body>
    </Modal>
  );
};

export default UploadCVModal;
