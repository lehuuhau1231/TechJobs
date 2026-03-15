import React, { useContext, useEffect, useRef, useState } from "react";
import {
  Container,
  Row,
  Col,
  Card,
  Button,
  Form,
  Image,
  Alert,
} from "react-bootstrap";
import { authApis, endpoints } from "../../configs/Apis";
import Loading from "../layout/Loading";
import {
  User,
  Mail,
  Phone,
  MapPin,
  File,
  Upload,
  FileText,
} from "lucide-react";
import cookies from "react-cookies";
import Header from "../layout/Header";
import UploadCVInterface from "./UploadCVInterface";
import UploadCVModal from "./UploadCVModal";

const Profile = () => {
  const [profile, setProfile] = useState(null);
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState({ type: "", content: "" });
  const [token, setToken] = useState(cookies.load("token"));
  const [showReplaceCVModal, setShowReplaceCVModal] = useState(false);

  useEffect(() => {
    const loadProfile = async () => {
      setLoading(true);
      try {
        const res = await authApis(token).get(endpoints.profile);
        setProfile(res.data);
      } catch (ex) {
        console.error("Lỗi khi tải thông tin cá nhân:", ex);
        setMessage({
          type: "danger",
          content: "Không thể tải thông tin cá nhân. Vui lòng thử lại sau.",
        });
      } finally {
        setLoading(false);
      }
    };

    loadProfile();
  }, []);

  const openCV = (url) => {
    window.open(url, "_blank");
  };

  return (
    <>
      <Header />
      <Container className='my-5'>
        {message.content && (
          <Alert
            variant={message.type}
            dismissible
            onClose={() => setMessage({ type: "", content: "" })}
          >
            {message.content}
          </Alert>
        )}

        {profile && (
          <Row>
            <Col md={4}>
              <Card className='mb-4 shadow-sm'>
                <Card.Body className='text-center'>
                  <div className='mb-4'>
                    <Image
                      src={profile.avatar || "https://via.placeholder.com/150"}
                      alt={profile.fullName}
                      roundedCircle
                      className='img-thumbnail'
                      style={{
                        width: "150px",
                        height: "150px",
                        objectFit: "cover",
                      }}
                    />
                  </div>
                  <h4>{profile.fullName}</h4>
                  <p className='text-muted'>
                    {profile.candidateId ? `ID: ${profile.candidateId}` : ""}
                  </p>

                  <div className='d-flex align-items-center justify-content-center mb-2'>
                    <Mail size={18} className='text-primary me-2' />
                    <span>{profile.email}</span>
                  </div>
                  <div className='d-flex align-items-center justify-content-center mb-2'>
                    <Phone size={18} className='text-primary me-2' />
                    <span>{profile.phone}</span>
                  </div>
                  <div className='d-flex align-items-center justify-content-center'>
                    <MapPin size={18} className='text-primary me-2' />
                    <span>
                      {[profile.address, profile.district, profile.city]
                        .filter(Boolean)
                        .join(", ")}
                    </span>
                  </div>
                </Card.Body>
              </Card>

              {profile.cv ? (
                <Card className='shadow-sm'>
                  <Card.Body>
                    <h5 className='d-flex align-items-center'>
                      <FileText size={20} className='text-primary me-2' />
                      CV của bạn
                    </h5>
                    <p className='text-muted small'>
                      Nhấn vào nút bên dưới để xem CV
                    </p>
                    <Row>
                      <Col md={6}>
                        <Button
                          variant='outline-primary'
                          className='w-100'
                          onClick={() => openCV(profile.cv)}
                        >
                          <File size={16} className='me-2' />
                          Xem CV
                        </Button>
                      </Col>
                      <Col md={6}>
                        <Button
                          variant='primary'
                          className='w-100'
                          onClick={() => setShowReplaceCVModal(true)}
                        >
                          Thay đổi CV
                        </Button>
                      </Col>
                    </Row>
                  </Card.Body>
                </Card>
              ) : (
                <UploadCVInterface />
              )}
            </Col>

            <Col md={8}>
              <Card className='shadow-sm'>
                <Card.Body>
                  <h4 className='mb-4'>Thông tin cá nhân</h4>

                  <div className='mb-4'>
                    <h5 className='d-flex align-items-center'>
                      <User size={18} className='text-primary me-2' />
                      Giới thiệu bản thân
                    </h5>
                    <Card className='bg-light border-0'>
                      <Card.Body>
                        {profile.selfDescription || (
                          <span className='text-muted'>
                            Chưa có thông tin giới thiệu bản thân.
                          </span>
                        )}
                      </Card.Body>
                    </Card>
                  </div>

                  <Row className='mb-4'>
                    <Col md={6}>
                      <div className='mb-3'>
                        <h6 className='text-muted'>Họ và tên</h6>
                        <p className='fw-medium'>{profile.fullName}</p>
                      </div>
                    </Col>
                    <Col md={6}>
                      <div className='mb-3'>
                        <h6 className='text-muted'>Email</h6>
                        <p className='fw-medium'>{profile.email}</p>
                      </div>
                    </Col>
                  </Row>

                  <Row>
                    <Col md={6}>
                      <div className='mb-3'>
                        <h6 className='text-muted'>Số điện thoại</h6>
                        <p className='fw-medium'>{profile.phone}</p>
                      </div>
                    </Col>
                    <Col md={6}>
                      <div className='mb-3'>
                        <h6 className='text-muted'>Địa chỉ</h6>
                        <p className='fw-medium'>
                          {[profile.address, profile.district, profile.city]
                            .filter(Boolean)
                            .join(", ")}
                        </p>
                      </div>
                    </Col>
                  </Row>

                  <div className='d-flex justify-content-end mt-3'>
                    <Button variant='outline-primary'>
                      Chỉnh sửa thông tin
                    </Button>
                  </div>
                </Card.Body>
              </Card>
            </Col>
          </Row>
        )}
        {
          <UploadCVModal
            showReplaceCVModal={showReplaceCVModal}
            setShowReplaceCVModal={setShowReplaceCVModal}
          />
        }
      </Container>
    </>
  );
};

export default Profile;
