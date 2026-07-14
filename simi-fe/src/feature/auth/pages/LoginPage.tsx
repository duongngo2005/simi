import { Link } from "react-router";
import Button from "../../../components/Button";
import styles from "./LoginPage.module.css";
import z from "zod";
import { useLogin } from "../hooks/useAuth";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { getServerError } from "../../../utils/getMessageError";

const loginSchema = z.object({
  email: z
  .string()
  .trim()
  .min(1, "Email không được để trống")
  .pipe(z.email("Email không đúng định dạng")),
  password: z
  .string()
  .min(1, "Mật khẩu không được để trống")
})

type LoginFormData = z.infer<typeof loginSchema>

const LoginPage = () => {

  const {mutate: login, isPending, error} = useLogin()

  const {
    register,
    handleSubmit,
    formState: {errors}
  } = useForm<LoginFormData>({
        resolver: zodResolver(loginSchema),
        mode: "onBlur"
  })

  const onSubmit = (data: LoginFormData) => {
    login(data)
  }

  const serverError = error ? getServerError(error) : null

  return (
    <div className={styles.loginPage}>
      <header className={styles.header}>
        <h1>Đăng nhập</h1>

        <p className={styles.description}>
          Chào mừng bạn quay lại với Simi
        </p>
      </header>

      {serverError && (
        <div className={styles.errorBanner}>{serverError}</div>
      )}

      <form onSubmit={handleSubmit(onSubmit)} noValidate className={styles.form}>
        <div className={styles.formField}>
          <label htmlFor="email">Email</label>

          <input
            type="email"
            id="email"
            autoComplete="email"
            placeholder="Nhập email của bạn"
            {...register("email")}
          />
          {errors.email && (
            <span className={styles.errorText}>
              {errors.email.message}
            </span>
          )}
        </div>

        <div className={styles.formField}>
          <div className={styles.passwordHeader}>
            <label htmlFor="password">Mật khẩu</label>

            <Link to="/forgot-password">Quên mật khẩu?</Link>
          </div>

          <input
            type="password"
            id="password"
            autoComplete="current-password"
            placeholder="Nhập mật khẩu"
            {...register("password")}
          />
          {errors.password && (
            <span className={styles.errorText}>
              {errors.password.message}
            </span>
          )}
        </div>

        <label className={styles.remember}>
          <input type="checkbox" name="remember" />
          <span>Ghi nhớ đăng nhập</span>
        </label>

        <Button disabled={isPending} type="submit" variant="primary" fullWidth>
          Đăng nhập
        </Button>
      </form>

      <div className={styles.divider}>
        <span>hoặc</span>
      </div>

      <div className={styles.registerSection}>
        <span>Chưa có tài khoản?</span>
        <Link to="/register">Đăng ký ngay</Link>
      </div>
    </div>
  );
};

export default LoginPage;