import React, { useState, useEffect } from "react";
import { Table, Button, Card, Spinner, Modal, Badge, Image } from "react-bootstrap";
import { CheckCircle, XCircle, Info, Building, Mail, Phone, MapPin } from "lucide-react";
import AdminLayout from "./AdminLayout";
import { authApis, endpoints } from "../../configs/Apis";
import cookies from "react-cookies";
import { toast } from "react-hot-toast";

const AdminEmployerApproval = () => {
  const [loading, setLoading] = useState(true);
  const [employers, setEmployers] = useState([]);
  const [showModal, setShowModal] = useState(false);
  const [selectedEmployer, setSelectedEmployer] = useState(null);
  const [actionType, setActionType] = useState("");
  const [processing, setProcessing] = useState(false);

  const fetchPendingEmployers = async () => {
    setLoading(true);
    try {
      const token = cookies.load("token");
      const response = await authApis(token).get(endpoints.admin_employer_pending);
      setEmployers(response.data);
    } catch (error) {
      console.error("Failed to fetch pending employers:", error);
      toast.error("Không thể tải danh sách nhà tuyển dụng");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchPendingEmployers();
  }, []);

  const handleAction = (employer, type) => {
    setSelectedEmployer(employer);
    setActionType(type);
    setShowModal(true);
  };

  const confirmAction = async () => {
    setProcessing(true);
    try {
      const token = cookies.load("token");
      const api = authApis(token);
      
      const url = actionType === "approve" 
        ? endpoints.approve_employer(selectedEmployer.id)
        : endpoints.reject_employer(selectedEmployer.id);

      await api.patch(url);
      
      toast.success(actionType === "approve" ? "Đã phê duyệt nhà tuyển dụng" : "Đã từ chối nhà tuyển dụng");
      setShowModal(false);
      fetchPendingEmployers();
    } catch (error) {
      console.error(`Failed to ${actionType} employer:`, error);
      toast.error("Thao tác thất bại.");
    } finally {
      setProcessing(false);
    }
  };

  return (
    <AdminLayout>
      <div className="mb-4">
        <h2 className="fw-bold">Phê Duyệt Nhà Tuyển Dụng</h2>
        <p className="text-muted">Kiểm tra thông tin pháp lý và xác thực tài khoản doanh nghiệp mới.</p>
      </div>

      <Card className="border-0 shadow-sm rounded-4 overflow-hidden">
        <Card.Body className="p-0">
          {loading ? (
            <div className="p-5 text-center">
              <Spinner animation="border" variant="primary" />
            </div>
          ) : employers.length === 0 ? (
            <div className="p-5 text-center text-muted">
              <Building size={48} className="mb-3 opacity-20" />
              <p>Không có yêu cầu phê duyệt mới.</p>
            </div>
          ) : (
            <Table responsive hover className="mb-0 align-middle">
              <thead className="bg-light">
                <tr>
                  <th className="px-4 py-3 border-0">Doanh nghiệp</th>
                  <th className="py-3 border-0">Mã số thuế</th>
                  <th className="py-3 border-0">Liên hệ</th>
                  <th className="py-3 border-0">Địa chỉ</th>
                  <th className="px-4 py-3 border-0 text-end">Thao tác</th>
                </tr>
              </thead>
              <tbody>
                {employers.map((emp) => (
                  <tr key={emp.id}>
                    <td className="px-4">
                      <div className="d-flex align-items-center gap-3">
                        <Image 
                          src={emp.avatar || "/default-company.png"} 
                          roundedCircle 
                          style={{ width: '45px', height: '45px', objectFit: 'cover' }}
                          className="border"
                        />
                        <div className="d-flex flex-column">
                          <span className="fw-bold text-dark">{emp.companyName}</span>
                          <span className="text-muted small">#{emp.id}</span>
                        </div>
                      </div>
                    </td>
                    <td>
                      <Badge bg="secondary" className="bg-opacity-10 text-secondary px-3 py-2 rounded-3">
                        {emp.taxCode}
                      </Badge>
                    </td>
                    <td>
                      <div className="d-flex flex-column gap-1">
                        <div className="d-flex align-items-center gap-2 text-muted small">
                          <Mail size={14} /> {emp.email}
                        </div>
                        <div className="d-flex align-items-center gap-2 text-muted small">
                          <Phone size={14} /> {emp.phone}
                        </div>
                      </div>
                    </td>
                    <td>
                      <div className="d-flex align-items-center gap-2 text-muted small max-w-200">
                        <MapPin size={14} className="flex-shrink-0" /> 
                        <span className="text-truncate">{emp.address}, {emp.district}, {emp.city}</span>
                      </div>
                    </td>
                    <td className="px-4 text-end">
                      <div className="d-flex justify-content-end gap-2">
                        <Button 
                          variant="outline-success" 
                          size="sm" 
                          className="rounded-circle p-2 shadow-none border-0"
                          onClick={() => handleAction(emp, "approve")}
                        >
                          <CheckCircle size={18} />
                        </Button>
                        <Button 
                          variant="outline-danger" 
                          size="sm" 
                          className="rounded-circle p-2 shadow-none border-0"
                          onClick={() => handleAction(emp, "reject")}
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
      <Modal show={showModal} onHide={() => !processing && setShowModal(false)} centered>
        <Modal.Header closeButton className="border-0 pb-0">
          <Modal.Title className="fw-bold">Phê duyệt doanh nghiệp</Modal.Title>
        </Modal.Header>
        <Modal.Body className="py-4 text-center">
          <div className={`p-4 rounded-circle d-inline-block mb-3 ${actionType === 'approve' ? 'bg-success' : 'bg-danger'} bg-opacity-10`}>
            {actionType === 'approve' ? <CheckCircle size={40} className="text-success" /> : <XCircle size={40} className="text-danger" />}
          </div>
          <h5 className="fw-bold mb-2">
            {actionType === 'approve' ? "Xác nhận phê duyệt" : "Xác nhận từ chối"}
          </h5>
          <p className="text-muted px-3">
            Bạn có chắc chắn muốn {actionType === 'approve' ? 'kích hoạt' : 'từ chối'} tài khoản cho 
            <strong> {selectedEmployer?.companyName}</strong>? 
            Hành động này sẽ gửi thông báo email đến doanh nghiệp.
          </p>
        </Modal.Body>
        <Modal.Footer className="border-0 pt-0 justify-content-center pb-4">
          <Button variant="light" className="px-4 rounded-pill" onClick={() => setShowModal(false)} disabled={processing}>
            Hủy bỏ
          </Button>
          <Button 
            variant={actionType === "approve" ? "success" : "danger"} 
            onClick={confirmAction}
            disabled={processing}
            className="px-4 rounded-pill shadow-sm"
          >
            {processing ? <Spinner animation="border" size="sm" /> : "Xác nhận"}
          </Button>
        </Modal.Footer>
      </Modal>

      <style>{`
        .max-w-200 {
          max-width: 250px;
        }
        tbody tr:hover {
          background-color: #f8fafc !important;
        }
      `}</style>
    </AdminLayout>
  );
};

export default AdminEmployerApproval;
