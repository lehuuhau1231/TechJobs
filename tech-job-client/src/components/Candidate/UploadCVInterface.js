import { Upload } from "lucide-react";
import { useState } from "react";
import { Button, Card, Form } from "react-bootstrap";
import cookies from "react-cookies";
import { authApis, endpoints } from "../../configs/Apis";

const UploadCVInterface = () => {
  const [profile, setProfile] = useState(null);
  const [loading, setLoading] = useState(false);
  const [uploadLoading, setUploadLoading] = useState(false);
  const [cvFile, setCvFile] = useState(null);
  const [message, setMessage] = useState({ type: "", content: "" });

  const [token, setToken] = useState(cookies.load("token"));
  const handleFileChange = (e) => {
    const file = e.target.files[0];
    if (file && file.type === "application/pdf") {
      setCvFile(file);
      setMessage({ type: "", content: "" });
    } else {
      setCvFile(null);
      setMessage({
        type: "danger",
        content: "Vui lòng chọn file PDF.",
      });
    }
  };

  const handleUploadCV = async () => {
    console.log("Uploading CV:", cvFile);
    if (!cvFile) return;

    setUploadLoading(true);
    setMessage({ type: "", content: "" });

    try {
      const formData = new FormData();
      formData.append("cvFile", cvFile);

      const res = await authApis(token).patch(
        `${endpoints.upload_cv}`,
        formData,
        {
          headers: {
            "Content-Type": "multipart/form-data",
          },
        },
      );

      if (res.status === 200 || res.status === 201) {
        setMessage({
          type: "success",
          content: "CV đã được tải lên thành công!",
        });

        // Refresh profile data
        const profileRes = await authApis(token).get(endpoints.profile);
        setProfile(profileRes.data);
        setCvFile(null);
      }
    } catch (ex) {
      console.error("Lỗi khi tải CV lên:", ex);
      setMessage({
        type: "danger",
        content: "Không thể tải CV lên. Vui lòng thử lại sau.",
      });
    } finally {
      setUploadLoading(false);
    }
  };
  return (
    <Card className='shadow-sm'>
      <Card.Body>
        <h5 className='d-flex align-items-center'>
          <Upload size={20} className='text-primary me-2' />
          Tải lên CV
        </h5>
        <p className='text-muted small'>
          Tải lên CV của bạn để nhà tuyển dụng xem
        </p>
        <Form.Group controlId='formFile' className='mb-3'>
          <Form.Control type='file' accept='.pdf' onChange={handleFileChange} />
          <Form.Text className='text-muted'>Chỉ chấp nhận file PDF.</Form.Text>
        </Form.Group>
        <Button
          variant='primary'
          className='w-100'
          onClick={handleUploadCV}
          disabled={!cvFile || uploadLoading}
        >
          {uploadLoading ? "Đang tải lên..." : "Tải lên CV"}
        </Button>
      </Card.Body>
    </Card>
  );
};

export default UploadCVInterface;
