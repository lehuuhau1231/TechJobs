import React, { useEffect, useState, useContext } from "react";
import {
  Container,
  Row,
  Col,
  Card,
  Table,
  Badge,
  Button,
  ButtonGroup,
  Alert,
} from "react-bootstrap";
import { format } from "date-fns";
import { vi } from "date-fns/locale";
import { Briefcase, Calendar, AlertCircle } from "lucide-react";
import { authApis, endpoints } from "../../configs/Apis";
import cookies from "react-cookies";
import { MyUserContext } from "../Context/MyContext";
import Loading from "../layout/Loading";
import Header from "../layout/Header";
import "../styles/common.css";
import { useNavigate } from "react-router-dom";

import "../styles/jobTracking.css";

const JobTracking = () => {
  const [jobs, setJobs] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [currentStatus, setCurrentStatus] = useState("PENDING");
  const [user] = useContext(MyUserContext);
  const navigate = useNavigate();

  useEffect(() => {
    if (!user) {
      setError("Vui lòng đăng nhập để xem danh sách công việc");
      setLoading(false);
      return;
    }

    fetchJobs(currentStatus);
  }, [currentStatus, user]);

  const fetchJobs = async (status) => {
    try {
      setLoading(true);
      setError(null);

      const token = cookies.load("token");
      const response = await authApis(token).get(
        `${endpoints.job_tracking}/${status}`
      );

      setJobs(response.data);
      setLoading(false);
    } catch (err) {
      console.error("Error fetching applications:", err);
      setError("Không thể tải danh sách công việc. Vui lòng thử lại sau.");
      setLoading(false);
    }
  };

  const handleStatusChange = (status) => {
    setCurrentStatus(status);
  };

  const formatDate = (dateString) => {
    try {
      const date = new Date(dateString);
      return format(date, "dd/MM/yyyy HH:mm", { locale: vi });
    } catch (error) {
      return dateString;
    }
  };

  const getStatusLabel = (status) => {
    switch (status) {
      case "APPROVED": return "Đã duyệt";
      case "CANCELED": return "Đã hủy";
      case "REJECTED": return "Bị từ chối";
      default: return "Đang chờ xử lý";
    }
  };

  const getStatusClass = (status) => {
    switch (status) {
      case "APPROVED": return "status-badge-approved";
      case "CANCELED": return "status-badge-canceled";
      case "REJECTED": return "status-badge-rejected";
      default: return "status-badge-pending";
    }
  };

  return (
    <div className="tracking-container">
      <Header />
      <Container>
        <div className="tracking-header-section mt-4">
          <h2 className="tracking-title">Theo dõi công việc đã đăng</h2>
          <p className="tracking-subtitle">Quản lý và theo dõi trạng thái các tin tuyển dụng của bạn</p>
        </div>

        {/* Status Navigation */}
        <div className="status-nav">
          <button
            className={`status-nav-item ${currentStatus === "PENDING" ? "active" : ""}`}
            onClick={() => handleStatusChange("PENDING")}
          >
            Đang chờ xử lý
          </button>
          <button
            className={`status-nav-item ${currentStatus === "APPROVED" ? "active" : ""}`}
            onClick={() => handleStatusChange("APPROVED")}
          >
            Đã duyệt
          </button>
          <button
            className={`status-nav-item ${currentStatus === "REJECTED" ? "active" : ""}`}
            onClick={() => handleStatusChange("REJECTED")}
          >
            Bị từ chối
          </button>
          <button
            className={`status-nav-item ${currentStatus === "CANCELED" ? "active" : ""}`}
            onClick={() => handleStatusChange("CANCELED")}
          >
            Đã hủy
          </button>
        </div>

        <div className="tracking-table-card">
          {loading ? (
            <div className="text-center py-5">
              <Loading />
            </div>
          ) : error ? (
            <Alert variant="danger" className="rounded-3 border-0 shadow-sm">{error}</Alert>
          ) : jobs.length === 0 ? (
            <div className="text-center py-5">
              <AlertCircle size={48} className="text-muted mb-3 opacity-25" />
              <p className="text-muted fw-bold">Không có công việc nào ở trạng thái này</p>
            </div>
          ) : (
            <div className="table-responsive">
              <table className="tracking-table">
                <thead>
                  <tr>
                    <th style={{ width: '80px' }}>STT</th>
                    <th style={{ width: '40rem' }}>Tên công việc</th>
                    <th>Ngày tạo</th>
                    <th>Trạng thái</th>
                    <th className="text-end">Hành động</th>
                  </tr>
                </thead>
                <tbody>
                  {jobs.map((job, index) => (
                    <tr key={job.id}>
                      <td>{index + 1}</td>
                      <td>
                        <div className="d-flex align-items-center gap-2">
                          <div className="p-2 bg-light rounded-3">
                            <Briefcase size={18} className="text-primary" />
                          </div>
                          <div>
                            <div>{job.title}</div>
                            {currentStatus === "REJECTED" && (
                              <div className="text-danger small mt-1" style={{ maxWidth: '450px', whiteSpace: 'normal' }}>
                                {job.rejectReason && <div><strong>Lý do chung:</strong> {job.rejectReason}</div>}
                                {job.fieldErrors && (() => {
                                  try {
                                    const errors = JSON.parse(job.fieldErrors);
                                    const errorEntries = Object.entries(errors).filter(([_, v]) => v);
                                    if (errorEntries.length > 0) {
                                      const translateField = (field) => {
                                        switch(field) {
                                          case 'title': return 'Tiêu đề';
                                          case 'description': return 'Mô tả';
                                          case 'jobRequire': return 'Yêu cầu';
                                          case 'benefits': return 'Quyền lợi';
                                          default: return field;
                                        }
                                      };
                                      return (
                                        <div className="mt-1 ps-2 border-start border-danger" style={{ fontSize: '0.82rem' }}>
                                          <span className="fw-semibold">Trường lỗi:</span>
                                          <ul className="mb-0 ps-3 mt-0" style={{ listStyleType: 'disc' }}>
                                            {errorEntries.map(([k, v]) => (
                                              <li key={k}>
                                                <strong>{translateField(k)}</strong>: {v}
                                              </li>
                                            ))}
                                          </ul>
                                        </div>
                                      );
                                    }
                                  } catch (e) {
                                    console.error(e);
                                  }
                                  return null;
                                })()}
                              </div>
                            )}
                          </div>
                        </div>
                      </td>
                      <td>
                        <div className="d-flex align-items-center gap-2 text-secondary small">
                          <Calendar size={16} />
                          {formatDate(job.createdDate)}
                        </div>
                      </td>
                      <td>
                        <span className={`status-badge ${getStatusClass(currentStatus)}`}>
                          {getStatusLabel(currentStatus)}
                        </span>
                      </td>
                      <td className="text-end">
                        <button
                          className="view-detail-btn"
                          onClick={() => navigate(`/job-detail/${job.id}`)}
                        >
                          Chi tiết
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      </Container>
    </div>
  );
};

export default JobTracking;
