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

  const getStatusVariant = (status) => {
    switch (status) {
      case "APPROVED":
        return "success";
      case "CANCELED":
        return "danger";
      case "PENDING":
      default:
        return "warning";
    }
  };

  return (
    <>
      <Header />
      <Container className='my-4'>
        <h2 className='mb-4'>Theo dõi đơn ứng tuyển</h2>

        <Card className='mb-4'>
          <Card.Body>
            <Row className='mb-3'>
              <Col>
                <ButtonGroup>
                  <Button
                    variant={
                      currentStatus === "PENDING"
                        ? "primary"
                        : "outline-primary"
                    }
                    style={{ width: "150px" }}
                    onClick={() => handleStatusChange("PENDING")}
                  >
                    Đang chờ xử lý
                  </Button>
                  <Button
                    variant={
                      currentStatus === "APPROVED"
                        ? "primary"
                        : "outline-primary"
                    }
                    style={{ width: "150px" }}
                    onClick={() => handleStatusChange("APPROVED")}
                  >
                    Đã duyệt
                  </Button>
                  <Button
                    variant={
                      currentStatus === "CANCELED"
                        ? "primary"
                        : "outline-primary"
                    }
                    style={{ width: "150px" }}
                    onClick={() => handleStatusChange("CANCELED")}
                  >
                    Đã hủy
                  </Button>
                </ButtonGroup>
              </Col>
            </Row>

            {loading ? (
              <div className='text-center py-4'>
                <Loading />
              </div>
            ) : error ? (
              <Alert variant='danger'>{error}</Alert>
            ) : applications.length === 0 ? (
              <Alert variant='info' className='d-flex align-items-center'>
                <AlertCircle className='me-2' />
                Không có đơn ứng tuyển nào với trạng thái "
                {currentStatus === "PENDING"
                  ? "Đang chờ xử lý"
                  : currentStatus === "APPROVED"
                  ? "Đã duyệt"
                  : "Đã hủy"}
                "
              </Alert>
            ) : (
              <Table responsive hover>
                <thead>
                  <tr>
                    <th>STT</th>
                    <th>Vị trí công việc</th>
                    <th>Ngày ứng tuyển</th>
                    <th>Trạng thái</th>
                    <th>Hành động</th>
                  </tr>
                </thead>
                <tbody>
                  {applications.map((app, index) => (
                    <tr key={app.applicationId}>
                      <td>{index + 1}</td>
                      <td>
                        <div className='d-flex align-items-center'>
                          <Briefcase size={16} className='me-2 text-muted' />
                          <div>{app.title}</div>
                        </div>
                      </td>
                      <td>
                        <div className='d-flex align-items-center'>
                          <Calendar size={16} className='me-2 text-muted' />
                          <div>{formatDate(app.appliedDate)}</div>
                        </div>
                      </td>
                      <td>
                        <Badge bg={getStatusVariant(currentStatus)}>
                          {currentStatus === "PENDING"
                            ? "Đang chờ xử lý"
                            : currentStatus === "APPROVED"
                            ? "Đã duyệt"
                            : "Đã hủy"}
                        </Badge>
                      </td>
                      <td>
                        <Button
                          variant='outline-primary'
                          size='sm'
                          onClick={() => navigate(`/job-detail/${app.jobId}`)}
                        >
                          Xem chi tiết
                        </Button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </Table>
            )}
          </Card.Body>
        </Card>
      </Container>
    </>
  );
};

export default ApplicationTracking;
