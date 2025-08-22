import "../styles/alertSuccess.css";
const AlertSuccess = ({ message, type }) => {
  return (
    <div className={`alert-box ${type}`}>
      <div className='alert-content'>
        <div className='alert-icon'>
          <i className='bi bi-check2-circle'></i>
        </div>
        <span className='alert-text'>{message}</span>
        <button className='alert-close'>&times;</button>
      </div>
    </div>
  );
};

export default AlertSuccess;
