import React from "react";
import { Link, useNavigate, useLocation } from "react-router-dom";
import { Navbar, Nav, Container, Row, Col } from "react-bootstrap";
import {
  LayoutDashboard,
  Briefcase,
  Building2,
  LogOut,
  Settings,
  Bell,
} from "lucide-react";
import cookies from "react-cookies";
import "../../components/styles/common.css";

const AdminLayout = ({ children }) => {
  const navigate = useNavigate();
  const location = useLocation();

  const handleLogout = () => {
    cookies.remove("token");
    window.location.href = "/login";
  };

  const menuItems = [
    {
      title: "Duyệt tin tuyển dụng",
      icon: <Briefcase size={20} />,
      path: "/admin/jobs",
    },
    {
      title: "Duyệt nhà tuyển dụng",
      icon: <Building2 size={20} />,
      path: "/admin/employers",
    },
    // { title: "Cài đặt", icon: <Settings size={20} />, path: "/admin/settings" },
  ];

  return (
    <div className='admin-container'>
      <Row className='g-0'>
        {/* Sidebar */}
        <Col
          md={2}
          className='bg-white border-end vh-100 sticky-top d-none d-md-block shadow-sm'
        >
          <div className='p-4 border-bottom mb-4'>
            <h4 className='text-primary fw-bold mb-0'>TechJobs Admin</h4>
          </div>
          <Nav className='flex-column px-3'>
            {menuItems.map((item, index) => (
              <Nav.Link
                key={index}
                as={Link}
                to={item.path}
                className={`d-flex align-items-center gap-3 p-3 rounded-3 mb-2 admin-nav-link ${
                  location.pathname === item.path ? "active" : "text-secondary"
                }`}
              >
                {item.icon}
                <span className='fw-medium'>{item.title}</span>
              </Nav.Link>
            ))}
            <hr className='my-4 text-muted' />
            <Nav.Link
              className='d-flex align-items-center gap-3 p-3 rounded-3 text-danger admin-nav-link'
              onClick={handleLogout}
            >
              <LogOut size={20} />
              <span className='fw-medium'>Đăng xuất</span>
            </Nav.Link>
          </Nav>
        </Col>

        {/* Main Content */}
        <Col md={10} className='bg-light min-vh-100'>
          <Navbar
            bg='white'
            className='border-bottom px-4 shadow-sm sticky-top'
          >
            <Container fluid>
              <Navbar.Brand className='d-md-none text-primary fw-bold'>
                Admin
              </Navbar.Brand>
              <div className='ms-auto d-flex align-items-center gap-4'>
                <div className='d-flex align-items-center gap-2'>
                  <div
                    className='rounded-circle bg-primary text-white d-flex align-items-center justify-content-center'
                    style={{ width: "40px", height: "40px" }}
                  >
                    AD
                  </div>
                </div>
              </div>
            </Container>
          </Navbar>
          <Container fluid className='p-4'>
            {children}
          </Container>
        </Col>
      </Row>

      <style>{`
        .admin-nav-link {
          transition: all 0.2s ease;
        }
        .admin-nav-link:hover {
          background-color: var(--border-subtle);
          color: var(--primary-color) !important;
        }
        .admin-nav-link.active {
          background-color: rgba(79, 70, 229, 0.1);
          color: var(--primary-color) !important;
        }
        .cursor-pointer {
          cursor: pointer;
        }
      `}</style>
    </div>
  );
};

export default AdminLayout;
