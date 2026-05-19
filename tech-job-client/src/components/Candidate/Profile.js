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
import toast from "react-hot-toast";

import "../styles/profile.css";

const Profile = () => {
  const [profile, setProfile] = useState(null);
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState({ type: "", content: "" });
  const [token, setToken] = useState(cookies.load("token"));
  const [showCVModal, setShowCVModal] = useState(false);

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

  useEffect(() => {
    if (message.content) {
      toast.success(message.content);
    }
  }, [message]);

  if (loading) return <Loading />;

  return (
    <div className='profile-container'>
      <Header />
      <Container>
        {profile && (
          <Row className='g-4 mt-2'>
            {/* Left Sidebar */}
            <Col lg={4}>
              <div className='profile-card-left'>
                <div className='profile-avatar-wrapper'>
                  <Image
                    src={profile.avatar || "https://via.placeholder.com/150"}
                    alt={profile.fullName}
                    className='profile-avatar'
                  />
                </div>
                <h2 className='profile-name'>{profile.fullName}</h2>

                <div className='mt-4 pt-4 border-top'>
                  <div className='profile-info-item'>
                    <Mail size={18} />
                    <span>{profile.email}</span>
                  </div>
                  <div className='profile-info-item'>
                    <Phone size={18} />
                    <span>{profile.phone}</span>
                  </div>
                  <div className='profile-info-item'>
                    <MapPin size={18} />
                    <span>
                      {[profile.address, profile.district, profile.city]
                        .filter(Boolean)
                        .join(", ")}
                    </span>
                  </div>
                </div>

                {/* CV Section */}
                <div className='cv-card text-start'>
                  <h6 className='d-flex align-items-center gap-2 mb-3'>
                    <FileText size={18} className='text-primary' />
                    Hồ sơ CV
                  </h6>
                  {profile.cv ? (
                    <div className='d-grid gap-2'>
                      <button
                        className='profile-action-btn btn-outline-custom w-100 d-flex align-items-center justify-content-center gap-2'
                        onClick={() => openCV(profile.cv)}
                        style={{ background: "transparent" }}
                      >
                        <File size={16} /> Xem CV hiện tại
                      </button>
                      <button
                        className='profile-action-btn btn-primary-custom w-100'
                        onClick={() => setShowCVModal(true)}
                      >
                        Cập nhật CV mới
                      </button>
                    </div>
                  ) : (
                    <div className='text-center py-2'>
                      <UploadCVInterface
                        setMessage={setMessage}
                        setShowModal={setShowCVModal}
                      />
                    </div>
                  )}
                </div>
              </div>
            </Col>

            {/* Main Content */}
            <Col lg={8}>
              <div className='profile-main-content'>
                <h3 className='section-title'>
                  <User size={22} />
                  Thông tin cá nhân
                </h3>

                <div className='mb-5'>
                  <div className='info-label'>Giới thiệu bản thân</div>
                  <div className='bio-box'>
                    {profile.selfDescription || (
                      <span className='text-muted italic'>
                        Chưa có thông tin giới thiệu bản thân. Hãy thêm giới
                        thiệu để nhà tuyển dụng hiểu rõ hơn về bạn.
                      </span>
                    )}
                  </div>
                </div>

                <div className='row g-4'>
                  <Col md={6}>
                    <div className='info-label'>Họ và tên</div>
                    <div className='info-value'>{profile.fullName}</div>
                  </Col>
                  <Col md={6}>
                    <div className='info-label'>Email liên hệ</div>
                    <div className='info-value'>{profile.email}</div>
                  </Col>
                  <Col md={6}>
                    <div className='info-label'>Số điện thoại</div>
                    <div className='info-value'>{profile.phone}</div>
                  </Col>
                  <Col md={6}>
                    <div className='info-label'>Địa chỉ hiện tại</div>
                    <div className='info-value'>
                      {[profile.address, profile.district, profile.city]
                        .filter(Boolean)
                        .join(", ") || "Chưa cập nhật"}
                    </div>
                  </Col>
                </div>

                <div className='d-flex justify-content-end mt-5 pt-4 border-top'>
                  <button className='profile-action-btn btn-primary-custom d-flex align-items-center gap-2'>
                    Chỉnh sửa hồ sơ
                  </button>
                </div>
              </div>
            </Col>
          </Row>
        )}

        {showCVModal && (
          <UploadCVModal
            showCVModal={showCVModal}
            setShowCVModal={setShowCVModal}
            setMessage={setMessage}
          />
        )}
      </Container>
    </div>
  );
};

export default Profile;
