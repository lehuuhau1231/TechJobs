import React, { useState, useEffect } from "react";
import {
  Table,
  Button,
  Badge,
  Card,
  Spinner,
  Modal,
  Image,
} from "react-bootstrap";
import {
  CheckCircle,
  XCircle,
  Eye,
  AlertCircle,
  MapPin,
  DollarSign,
  Briefcase,
} from "lucide-react";
import AdminLayout from "./AdminLayout";
import { authApis, endpoints } from "../../configs/Apis";
import cookies from "react-cookies";
import { toast } from "react-hot-toast";

const AdminJobReview = () => {
  const [loading, setLoading] = useState(true);
  const [jobs, setJobs] = useState([]);
  const [showModal, setShowModal] = useState(false);
  const [selectedJob, setSelectedJob] = useState(null);
  const [actionType, setActionType] = useState(""); // "approve" or "reject"
  const [processing, setProcessing] = useState(false);

  const fetchPendingJobs = async () => {
    setLoading(true);
    try {
      const token = cookies.load("token");
      const response = await authApis(token).get(endpoints.admin_job_pending);
      setJobs(response.data);
    } catch (error) {
      console.error("Failed to fetch pending jobs:", error);
      toast.error("Không thể tải danh sách tin tuyển dụng");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchPendingJobs();
  }, []);

  const handleAction = (job, type) => {
    setSelectedJob(job);
    setActionType(type);
    setShowModal(true);
  };

  const confirmAction = async () => {
    setProcessing(true);
    try {
      const token = cookies.load("token");
      const api = authApis(token);

      const url =
        actionType === "approve"
          ? endpoints.approve_job(selectedJob.id)
          : endpoints.reject_job(selectedJob.id);

      await api.patch(url);

      toast.success(
        actionType === "approve"
          ? "Đã phê duyệt tin tuyển dụng"
          : "Đã từ chối tin tuyển dụng",
      );
      setShowModal(false);
      fetchPendingJobs();
    } catch (error) {
      console.error(`Failed to ${actionType} job:`, error);
      toast.error("Thao tác thất bại. Vui lòng thử lại.");
    } finally {
      setProcessing(false);
    }
  };

  return (
    <AdminLayout>
      <div className='mb-4'>
        <h2 className='fw-bold'>Xét Duyệt Tin Tuyển Dụng</h2>
        <p className='text-muted'>
          Danh sách các tin tuyển dụng mới đang chờ xác nhận để hiển thị công
          khai.
        </p>
      </div>

      <Card className='border-0 shadow-sm rounded-4 overflow-hidden'>
        <Card.Body className='p-0'>
          {loading ? (
            <div className='p-5 text-center'>
              <Spinner animation='border' variant='primary' />
            </div>
          ) : jobs.length === 0 ? (
            <div className='p-5 text-center text-muted'>
              <AlertCircle size={48} className='mb-3 opacity-20' />
              <p>Không có tin tuyển dụng nào cần xét duyệt.</p>
            </div>
          ) : (
            <Table responsive hover className='mb-0 align-middle'>
              <thead className='bg-light'>
                <tr>
                  <th className='px-4 py-3 border-0'>Tin tuyển dụng</th>
                  <th className='py-3 border-0'>Công ty</th>
                  <th className='py-3 border-0 text-center'>Trạng thái</th>
                  <th className='px-4 py-3 border-0 text-end'>Thao tác</th>
                </tr>
              </thead>
              <tbody>
                {jobs.map((job) => (
                  <tr key={job.id}>
                    <td className='px-4'>
                      <div className='d-flex flex-column'>
                        <span className='fw-bold text-primary'>
                          {job.title}
                        </span>
                      </div>
                    </td>
                    <td>
                      <div className='d-flex align-items-center gap-2'>
                        <Image
                          src={job.image || "/default-company.png"}
                          roundedCircle
                          style={{
                            width: "30px",
                            height: "30px",
                            objectFit: "cover",
                          }}
                        />
                        <span className='fw-medium'>{job.companyName}</span>
                      </div>
                    </td>
                    <td className='text-center'>
                      <Badge
                        bg='warning'
                        className='text-dark bg-opacity-10 px-3 py-2 rounded-pill'
                      >
                        Chờ duyệt
                      </Badge>
                    </td>
                    <td className='px-4 text-end'>
                      <div className='d-flex justify-content-end gap-2'>
                        <Button
                          variant='outline-info'
                          size='sm'
                          className='rounded-circle p-2 shadow-none border-0'
                          onClick={() =>
                            window.open(`/job-detail/${job.id}`, "_blank")
                          }
                        >
                          <Eye size={18} />
                        </Button>
                        <Button
                          variant='outline-success'
                          size='sm'
                          className='rounded-circle p-2 shadow-none border-0'
                          onClick={() => handleAction(job, "approve")}
                        >
                          <CheckCircle size={18} />
                        </Button>
                        <Button
                          variant='outline-danger'
                          size='sm'
                          className='rounded-circle p-2 shadow-none border-0'
                          onClick={() => handleAction(job, "reject")}
                        >
                          <XCircle size={18} />
                        </Button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </Table>
          )}
        </Card.Body>
      </Card>

      {/* Confirmation Modal */}
      <Modal
        show={showModal}
        onHide={() => !processing && setShowModal(false)}
        centered
      >
        <Modal.Header closeButton className='border-0 pb-0'>
          <Modal.Title className='fw-bold'>Xác nhận phê duyệt</Modal.Title>
        </Modal.Header>
        <Modal.Body className='py-4 text-center'>
          <div
            className={`p-4 rounded-circle d-inline-block mb-3 ${actionType === "approve" ? "bg-success" : "bg-danger"} bg-opacity-10`}
          >
            {actionType === "approve" ? (
              <CheckCircle size={40} className='text-success' />
            ) : (
              <XCircle size={40} className='text-danger' />
            )}
          </div>
          <h5 className='fw-bold mb-2'>
            {actionType === "approve"
              ? "Phê duyệt tin tuyển dụng"
              : "Từ chối tin tuyển dụng"}
          </h5>
          <p className='text-muted px-3'>
            Bạn có chắc chắn muốn{" "}
            {actionType === "approve" ? "hiển thị công khai" : "từ chối"} tin
            <strong> "{selectedJob?.title}"</strong> của{" "}
            <strong>{selectedJob?.companyName}</strong>?
          </p>
        </Modal.Body>
        <Modal.Footer className='border-0 pt-0 justify-content-center pb-4'>
          <Button
            variant='light'
            className='px-4 rounded-pill'
            onClick={() => setShowModal(false)}
            disabled={processing}
          >
            Hủy bỏ
          </Button>
          <Button
            variant={actionType === "approve" ? "success" : "danger"}
            onClick={confirmAction}
            disabled={processing}
            className='px-4 rounded-pill shadow-sm'
          >
            {processing ? <Spinner animation='border' size='sm' /> : "Xác nhận"}
          </Button>
        </Modal.Footer>
      </Modal>

      <style>{`
        .extra-small {
          font-size: 0.75rem;
        }
        tbody tr:hover {
          background-color: #f8fafc !important;
        }
      `}</style>
    </AdminLayout>
  );
};

export default AdminJobReview;
