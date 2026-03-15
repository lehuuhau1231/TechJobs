import { useState } from "react";
import { Modal } from "react-bootstrap";
import UploadCVInterface from "./UploadCVInterface";

const UploadCVModal = ({ showReplaceCVModal, setShowReplaceCVModal }) => {
  const handleClose = () => setShowReplaceCVModal(false);
  return (
    <Modal show={showReplaceCVModal} onHide={handleClose}>
      <Modal.Header closeButton>
        <Modal.Title>Thay thế CV</Modal.Title>
      </Modal.Header>
      <Modal.Body>
        <UploadCVInterface />
      </Modal.Body>
    </Modal>
  );
};

export default UploadCVModal;
