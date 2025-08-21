import { Spinner } from "react-bootstrap";
import "../styles/common.css";

const Loading = () => {
  return (
    <Spinner
      animation='border'
      className='primary-color'
      style={{
        margin: "auto",
        display: "block",
      }}
    />
  );
};

export default Loading;
