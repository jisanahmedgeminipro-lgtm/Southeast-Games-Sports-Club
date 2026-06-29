package bd.edu.seu.gamesclub.controller;

import bd.edu.seu.gamesclub.dto.ForgotPasswordRequest;
import bd.edu.seu.gamesclub.dto.OtpVerifyRequest;
import bd.edu.seu.gamesclub.dto.RegisterRequest;
import bd.edu.seu.gamesclub.dto.ResetPasswordRequest;
import bd.edu.seu.gamesclub.exception.BusinessException;
import bd.edu.seu.gamesclub.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Handles registration, OTP verification and the forgot/reset-password flows.
 * The login form itself is processed by Spring Security (not this controller).
 */
@Controller
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        if (!model.containsAttribute("registerForm")) {
            model.addAttribute("registerForm",
                    new RegisterRequest("", "", "", "", "", "", "", "", "", ""));
        }
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("registerForm") RegisterRequest form,
                           BindingResult binding, Model model, RedirectAttributes ra) {
        if (binding.hasErrors()) {
            return "auth/register";
        }
        try {
            authService.register(form);
        } catch (BusinessException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return "auth/register";
        }
        ra.addAttribute("email", form.email());
        ra.addFlashAttribute("successMessage", "We sent a verification code to your email.");
        return "redirect:/verify-otp";
    }

    @GetMapping("/verify-otp")
    public String verifyOtpForm(@RequestParam(required = false) String email, Model model) {
        model.addAttribute("email", email);
        return "auth/otp-verification";
    }

    @PostMapping("/verify-otp")
    public String verifyOtp(@RequestParam String email, @RequestParam String otp, RedirectAttributes ra) {
        try {
            authService.verifyRegistration(new OtpVerifyRequest(email, otp));
        } catch (BusinessException ex) {
            ra.addAttribute("email", email);
            ra.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/verify-otp";
        }
        return "redirect:/login?registered";
    }

    @PostMapping("/resend-otp")
    public String resendOtp(@RequestParam String email, RedirectAttributes ra) {
        try {
            authService.resendRegistrationOtp(email);
            ra.addFlashAttribute("successMessage", "A new code has been sent.");
        } catch (BusinessException ex) {
            ra.addFlashAttribute("errorMessage", ex.getMessage());
        }
        ra.addAttribute("email", email);
        return "redirect:/verify-otp";
    }

    @GetMapping("/forgot-password")
    public String forgotPasswordForm() {
        return "auth/forgot-password";
    }

    @PostMapping("/forgot-password")
    public String forgotPassword(@RequestParam String email, RedirectAttributes ra) {
        authService.initiatePasswordReset(new ForgotPasswordRequest(email));
        ra.addAttribute("email", email);
        ra.addFlashAttribute("successMessage", "If that email is registered, a reset code has been sent.");
        return "redirect:/reset-password";
    }

    @GetMapping("/reset-password")
    public String resetPasswordForm(@RequestParam(required = false) String email, Model model) {
        model.addAttribute("email", email);
        return "auth/reset-password";
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam String email, @RequestParam String otp,
                                @RequestParam String password, @RequestParam String confirmPassword,
                                RedirectAttributes ra) {
        try {
            authService.resetPassword(new ResetPasswordRequest(email, otp, password, confirmPassword));
        } catch (BusinessException ex) {
            ra.addAttribute("email", email);
            ra.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/reset-password";
        }
        return "redirect:/login?reset";
    }
}
