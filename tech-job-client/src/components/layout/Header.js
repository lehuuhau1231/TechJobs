import {
  Bell,
  Building2,
  MessageCircle,
  Search,
  Settings,
  TrendingUp,
  Users,
} from "lucide-react";
import { useContext, useEffect, useState } from "react";
import { Button, Container, Dropdown, Image } from "react-bootstrap";
import { MyUserContext } from "../Context/MyContext";
import "../styles/header.css";
import { useNavigate } from "react-router-dom";
import "bootstrap-icons/font/bootstrap-icons.css";

const Header = () => {
  const [user, dispatch] = useContext(MyUserContext);
  const [provinces, setProvinces] = useState([]);
  const [districts, setDistricts] = useState([]);
  const navigate = useNavigate();

  useEffect(() => {
    fetchProvinces();
  }, []);

  const fetchProvinces = async () => {
    try {
      const response = await fetch();
      const data = await response.json();
      console.log("Provinces:", data);
    } catch (error) {
      console.error("Error fetching provinces:", error);
    }
  };

  const fetchDistricts = async () => {
    try {
      const response = await fetch();
      const data = await response.json();
      console.log("Districts:", data);
    } catch (error) {
      console.error("Error fetching districts:", error);
    }
  };

  return (
    <Container>
      <div
        style={{
          padding: "12px 20px",
          backgroundColor: "#ffffff",
          borderBottom: "1px solid #e5e7eb",
          display: "flex",
          alignItems: "center",
          justifyContent: "space-between",
        }}
      >
        <div
          style={{ display: "flex", alignItems: "center", cursor: "pointer" }}
          onClick={() => navigate("/")}
        >
          <span style={{ fontSize: "20px", marginRight: "8px" }}>⚡</span>
          <span style={{ fontWeight: "bold", fontSize: "16px" }}>Tech Job</span>
        </div>

        <div style={{ display: "flex", alignItems: "center", gap: "12px" }}>
          <button className='button button-active'>
            <Search size={12} />
            Find Jobs
          </button>
          {/* <button className='button button-inactive'>
            <Users size={12} />
            Find Talent
          </button>
          <button className='button button-inactive'>
            <Building2 size={12} />
            Community
          </button>
          <button className='button button-inactive'>
            <TrendingUp size={12} />
            Upload Job
          </button> */}
          <Bell size={16} style={{ color: "#6b7280", cursor: "pointer" }} />
          <MessageCircle
            size={16}
            style={{ color: "#6b7280", cursor: "pointer" }}
          />
          <Settings size={16} style={{ color: "#6b7280", cursor: "pointer" }} />
          {user ? (
            <>
              <Dropdown align='end'>
                <Dropdown.Toggle
                  as='div'
                  id='dropdown-user'
                  bsPrefix='custom-toggle'
                  className='p-0 border-0 shadow-none bg-transparent'
                >
                  <Image
                    src={user.result.avatar}
                    alt='User Avatar'
                    className='image'
                  />
                </Dropdown.Toggle>

                <Dropdown.Menu>
                  <Dropdown.Item onClick={() => navigate("/profile")}>
                    <i className='bi bi-person me-2'></i>
                    Thông tin cá nhân
                  </Dropdown.Item>
                  <Dropdown.Divider />
                  <Dropdown.Item
                    onClick={() => {
                      dispatch({ type: "logout" });
                    }}
                    className='text-danger'
                  >
                    <i className='bi bi-box-arrow-right me-2'></i>
                    Đăng xuất
                  </Dropdown.Item>
                </Dropdown.Menu>
              </Dropdown>
            </>
          ) : (
            <>
              <Button className='button' onClick={() => navigate("/login")}>
                Đăng nhập
              </Button>
            </>
          )}
        </div>
      </div>
    </Container>
  );
};

export default Header;
