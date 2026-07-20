import { Link } from "react-router";
import Button from "../../../components/Button";
import styles from "./RegisterPage.module.css";
import z from "zod";
import { useRegister } from "../hooks/useAuth";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { getServerError } from "../../../utils/getMessageError";

const registerSchema = z.object({
  fullName: z.string().min(1, "Tên đầy đủ không được để trống"),
  email: z.string().trim().min(1, "Email không được để trống")
           .pipe(z.email("Email không đúng định dạng")),
  phoneNumber: z.string()               // ← THÊM MỚI
    .min(10, "Số điện thoại tối thiểu 10 số")
    .max(11, "Số điện thoại tối đa 11 số")
    .regex(/^[0-9]+$/, "Số điện thoại chỉ được chứa chữ số"),
  password: z.string().min(5, "Mật khẩu phải lớn hơn 5 ký tự"),
  confirm: z.string().min(1, "Xác nhận mật khẩu không được để trống"),
}).refine((data) => data.password === data.confirm, {
  message: "Mật khẩu xác nhận không khớp",
  path: ["confirm"]
});


type RegisterFormData = z.infer<typeof registerSchema>

const RegisterPage = () => {

  const {mutate: register, isPending, error} = useRegister()
  const serverError = error ? getServerError(error) : null 

  const {
    register: formRegister,
    handleSubmit,
    formState: {errors}
  } = useForm<RegisterFormData>({
      resolver: zodResolver(registerSchema),
      mode: "onBlur"
  })

  const onSubmit = (data: RegisterFormData) => {
    const {confirm, ...payload} = data
    register(payload)
  }

  return (
    <div className={styles.registerPage}>
      <header className={styles.header}>
        <h1>Đăng ký</h1>

        <p className={styles.description}>
          Tạo tài khoản để bắt đầu mua sắm và ký gửi cùng Simi
        </p>
      </header>

      {serverError && (
        <div className={styles.errorBanner}>{serverError}</div>
      )}

      <form onSubmit={handleSubmit(onSubmit)} className={styles.form}>
        <div className={styles.formField}>
          <label htmlFor="fullName">Họ và tên</label>

          <input
            type="text"
            id="fullName"
            autoComplete="name"
            placeholder="Nhập họ và tên"
            {...formRegister("fullName")}
          />
          {errors.fullName && (
            <span className={styles.errorText}>
              {errors.fullName.message}
            </span>
          )}
        </div>

        <div className={styles.formField}>
          <label htmlFor="phoneNumber">Số điện thoại</label>
          <input
            type="tel"
            id="phoneNumber"
            autoComplete="tel"
            placeholder="Nhập số điện thoại"
            {...formRegister("phoneNumber")}
          />
          {errors.phoneNumber && (
            <span className={styles.errorText}>
              {errors.phoneNumber.message}
            </span>
          )}
        </div>


        <div className={styles.formField}>
          <label htmlFor="email">Email</label>

          <input
            type="email"
            id="email"
            autoComplete="email"
            placeholder="Nhập email của bạn"
            {...formRegister("email")}
          />
          {errors.email && (
            <span className={styles.errorText}>
              {errors.email.message}
            </span>
          )}
        </div>

        <div className={styles.formField}>
          <label htmlFor="password">Mật khẩu</label>

          <input
            type="password"
            id="password"
            autoComplete="new-password"
            placeholder="Tạo mật khẩu"
            {...formRegister("password")}
          />
          {errors.password && (
            <span className={styles.errorText}>
              {errors.password.message}
            </span>
          )}
        </div>

        <div className={styles.formField}>
          <label htmlFor="confirmPassword">Xác nhận mật khẩu</label>

          <input
            type="password"
            id="confirmPassword"
            autoComplete="new-password"
            placeholder="Nhập lại mật khẩu"
            {...formRegister("confirm")}
          />
          {errors.confirm && (
            <span className={styles.errorText}>
              {errors.confirm.message}
            </span>
          )}
        </div>

        <label className={styles.policy}>
          <input type="checkbox" />

          <span>
            Tôi đồng ý với{" "}
            <Link to="/terms">điều khoản sử dụng</Link>
          </span>
        </label>

        <Button disabled={isPending} type="submit" variant="primary" fullWidth>
          Đăng ký
        </Button>
      </form>

      <div className={styles.divider}>
        <span>hoặc</span>
      </div>

      <div className={styles.loginSection}>
        <span>Đã có tài khoản?</span>

        <Link to="/login">Đăng nhập ngay</Link>
      </div>
    </div>
  );
};

export default RegisterPage;