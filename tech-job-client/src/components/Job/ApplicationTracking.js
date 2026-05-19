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

import "../styles/applicationTracking.css";

const ApplicationTracking = () => {
  const [applications, setApplications] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [currentStatus, setCurrentStatus] = useState("PENDING");
  const [user] = useContext(MyUserContext);
  const navigate = useNavigate();

  useEffect(() => {
    if (!user) {
      setError("Vui lòng đăng nhập để xem đơn ứng tuyển của bạn");
      setLoading(false);
      return;
    }

    fetchApplications(currentStatus);
  }, [currentStatus, user]);

  const fetchApplications = async (status) => {
    try {
      setLoading(true);
      setError(null);

      const token = cookies.load("token");
      const response = await authApis(token).get(
        `${endpoints.application}?status=${status}`
      );

      setApplications(response.data);
      setLoading(false);
    } catch (err) {
      console.error("Error fetching applications:", err);
      setError("Không thể tải danh sách đơn ứng tuyển. Vui lòng thử lại sau.");
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
      default: return "Đang chờ xử lý";
    }
  };

  const getStatusClass = (status) => {
    switch (status) {
      case "APPROVED": return "status-badge-approved";
      case "CANCELED": return "status-badge-canceled";
      default: return "status-badge-pending";
    }
  };

  return (
    <div className="tracking-container">
      <Header />
      <Container>
        <div className="tracking-header-section mt-4">
          <h2 className="tracking-title">Theo dõi đơn ứng tuyển</h2>
          <p className="tracking-subtitle">Xem trạng thái các công việc bạn đã ứng tuyển</p>
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
          ) : applications.length === 0 ? (
            <div className="text-center py-5">
              <AlertCircle size={48} className="text-muted mb-3 opacity-25" />
              <p className="text-muted fw-bold">Không có đơn ứng tuyển nào ở trạng thái này</p>
            </div>
          ) : (
            <div className="table-responsive">
              <table className="tracking-table">
                <thead>
                  <tr>
                    <th style={{ width: '80px' }}>STT</th>
                    <th>Vị trí công việc</th>
                    <th>Ngày ứng tuyển</th>
                    <th>Trạng thái</th>
                    <th className="text-end">Thao tác</th>
                  </tr>
                </thead>
                <tbody>
                  {applications.map((app, index) => (
                    <tr key={app.applicationId}>
                      <td>{index + 1}</td>
                      <td>
                        <div className="d-flex align-items-center gap-2">
                          <div className="p-2 bg-light rounded-3">
                            <Briefcase size={18} className="text-primary" />
                          </div>
                          <div>{app.title}</div>
                        </div>
                      </td>
                      <td>
                        <div className="d-flex align-items-center gap-2 text-secondary small">
                          <Calendar size={16} />
                          {formatDate(app.appliedDate)}
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
                          onClick={() => navigate(`/job-detail/${app.jobId}`)}
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

export default ApplicationTracking;
