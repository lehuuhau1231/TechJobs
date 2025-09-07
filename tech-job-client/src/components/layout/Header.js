import {
  BadgePlus,
  Bell,
  Building2,
  ClipboardClock,
  FileUser,
  LogOut,
  MessageCircle,
  Search,
  Settings,
  ThumbsUp,
  TrendingUp,
  User,
  Users,
} from "lucide-react";
import { useContext, useEffect, useState } from "react";
import { Button, Container, Dropdown, Image, Modal } from "react-bootstrap";
import { MyUserContext } from "../Context/MyContext";
import "../styles/header.css";
import "../styles/common.css";
import { useNavigate } from "react-router-dom";
import "bootstrap-icons/font/bootstrap-icons.css";
import cookies from "react-cookies";
import { authApis, endpoints } from "../../configs/Apis";
import ModalRequired from "./ModalRequired";

const Header = () => {
  const [user, dispatch] = useContext(MyUserContext);
  const navigate = useNavigate();
  const [activeTab, setActiveTab] = useState("search");
  const [showModalProfile, setShowModalProfile] = useState(false);
  const [showModalLogin, setShowModalLogin] = useState(false);

  const menuItems = {
    user: [
      {
        path: "/profile",
        icon: <User size={20} style={{ marginRight: "8px" }} />,
        text: "Thông tin cá nhân",
        isDivider: false,
        className: "",
        action: () => navigate("/profile"),
      },
      {
        path: "/application-tracking",
        icon: <ClipboardClock size={20} style={{ marginRight: "8px" }} />,
        text: "Theo dõi ứng tuyển",
        isDivider: false,
        className: "",
        action: () => navigate("/application-tracking"),
      },
      {
        path: "/application-tracking",
        icon: <ClipboardClock size={20} style={{ marginRight: "8px" }} />,
        text: "Thông tin cá nhân",
        isDivider: false,
        className: "",
        action: () => navigate("/profile"),
      },
      {
        isDivider: true,
      },
      {
        icon: <LogOut size={20} style={{ marginRight: "8px" }} />,
        text: "Đăng xuất",
        isDivider: false,
        className: "text-danger",
        action: () => dispatch({ type: "logout" }),
      },
    ],
    employer: [
      {
        path: "/create-job",
        icon: <BadgePlus size={20} style={{ marginRight: "8px" }} />,
        text: "Tạo công việc",
        isDivider: false,
        className: "",
        action: () => navigate("/create-job"),
      },
      {
        path: "/job-tracking",
        icon: <ClipboardClock size={20} style={{ marginRight: "8px" }} />,
        text: "Theo dõi công việc",
        isDivider: false,
        className: "",
        action: () => navigate("/job-tracking"),
      },
      {
        path: "/approve-job",
        icon: <FileUser size={20} style={{ marginRight: "8px" }} />,
        text: "Ứng viên ứng tuyển",
        isDivider: false,
        className: "",
        action: () => navigate("/approve-job"),
      },
      {
        isDivider: true,
      },
      {
        icon: <LogOut size={20} style={{ marginRight: "8px" }} />,
        text: "Đăng xuất",
        isDivider: false,
        className: "text-danger",
        action: () => dispatch({ type: "logout" }),
      },
    ],
    admin: [
      {
        path: "/admin/job-review",
        icon: <FileUser size={20} style={{ marginRight: "8px" }} />,
        text: "Xem xét công việc",
        isDivider: false,
        className: "",
        action: () => navigate("/admin/job-review"),
      },
      {
        path: "/admin/employer/pending",
        icon: <FileUser size={20} style={{ marginRight: "8px" }} />,
        text: "Xem xét nhà tuyển dụng",
        isDivider: false,
        className: "",
        action: () => navigate("/admin/employer/pending"),
      },
      {
        isDivider: true,
      },
      {
        icon: <LogOut size={20} style={{ marginRight: "8px" }} />,
        text: "Đăng xuất",
        isDivider: false,
        className: "text-danger",
        action: () => dispatch({ type: "logout" }),
      },
    ],
  };

  const handleTabChange = async (tab) => {
    setActiveTab(tab);
    const token = cookies.load("token");
    console.log("tab", tab);
    if (tab === "recommend" && token) {
      try {
        console.log("tab", tab);
        const response = await authApis(token).get(endpoints.check_cv);
        if (response.data.hasCV === false) {
          setShowModalProfile(true);
        } else {
          navigate("/job-recommendation");
        }
      } catch (error) {
        console.error("Error checking CV:", error);
      }
    } else if (tab === "search" && token) {
      navigate("/");
    } else if (tab === "recommend" && !token) {
      setShowModalLogin(true);
    }
  };

  return (
    <>
      <Container>
        {/* Login Modal */}
        {showModalLogin && (
          <ModalRequired
            message='Bạn cần đăng nhập để thực hiện hành động này.'
            redirectPath='/login?next=/job-recommendation'
            messageRedirect='Đến trang đăng nhập'
            showModal={showModalLogin}
            setShowModal={setShowModalLogin}
          />
        )}

        {showModalProfile && (
          <ModalRequired
            message='Bạn cần cập nhật hồ sơ để thực hiện hành động này.'
            redirectPath='/profile'
            messageRedirect='Đến trang hồ sơ'
            showModal={showModalProfile}
            setShowModal={setShowModalProfile}
          />
        )}

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
            style={{
              display: "flex",
              alignItems: "center",
              cursor: "pointer",
            }}
            onClick={() => navigate("/")}
          >
            <span style={{ fontSize: "20px", marginRight: "8px" }}>⚡</span>
            <span style={{ fontWeight: "bold", fontSize: "16px" }}>
              Tech Job
            </span>
          </div>

          <div style={{ display: "flex", alignItems: "center", gap: "12px" }}>
            <Button
              className={`${
                activeTab === "search" ? "button" : "button-tab-inactive"
              }`}
              onClick={() => handleTabChange("search")}
            >
              <Search size={12} />
              Tìm việc làm
            </Button>
            <Button
              className={`${
                activeTab === "recommend" ? "button" : "button-tab-inactive"
              }`}
              onClick={() => handleTabChange("recommend")}
            >
              <ThumbsUp size={12} />
              Việc làm phù hợp
            </Button>
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
            <Settings
              size={16}
              style={{ color: "#6b7280", cursor: "pointer" }}
            />
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
                      src={user.avatar}
                      alt='User Avatar'
                      className='image'
                    />
                  </Dropdown.Toggle>

                  <Dropdown.Menu>
                    {user.role === "CANDIDATE"
                      ? menuItems.user.map((item, index) =>
                          item.isDivider ? (
                            <Dropdown.Divider key={`divider-${index}`} />
                          ) : (
                            <Dropdown.Item
                              key={index}
                              onClick={item.action}
                              className={item.className}
                            >
                              {item.icon}
                              {item.text}
                            </Dropdown.Item>
                          )
                        )
                      : user.role === "EMPLOYER"
                      ? menuItems.employer.map((item, index) =>
                          item.isDivider ? (
                            <Dropdown.Divider key={`divider-${index}`} />
                          ) : (
                            <Dropdown.Item
                              key={index}
                              onClick={item.action}
                              className={item.className}
                            >
                              {item.icon}
                              {item.text}
                            </Dropdown.Item>
                          )
                        )
                      : null}
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
    </>
  );
};

export default Header;
