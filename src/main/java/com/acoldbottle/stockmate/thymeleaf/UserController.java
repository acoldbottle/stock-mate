package com.acoldbottle.stockmate.thymeleaf;

import com.acoldbottle.stockmate.api.user.dto.req.UserSignUpReq;
import com.acoldbottle.stockmate.api.user.service.UserService;
import com.acoldbottle.stockmate.exception.user.UserAlreadyExistsException;
import com.acoldbottle.stockmate.exception.user.UserPasswordMismatchException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/stockmate")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private static final String ERR_MSG = "errorMessage";

    @GetMapping("/login")
    public String login() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            return "redirect:/stockmate/portfolios";
        }
        return "login";
    }

    @GetMapping("/signup")
    public String signup() {
        return "signup";
    }

    @PostMapping("/signup")
    public String signupSubmit(@Valid @ModelAttribute UserSignUpReq user, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return validateUserSignUpReq(result, model);
        }

        try {
            userService.signUp(user);
        } catch (UserAlreadyExistsException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "signup";
        } catch (UserPasswordMismatchException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "signup";
        }

        return "redirect:/stockmate/login";
    }

    private String validateUserSignUpReq(BindingResult result, Model model) {
        String[] fields = {"username", "password", "passwordConfirm"};
        for (String field : fields) {
            if (result.hasFieldErrors(field)) {
                String errorMessage = result.getFieldErrors(field).stream()
                        .filter(e -> "NotBlank".equals(e.getCode()))
                        .findFirst()
                        .map(FieldError::getDefaultMessage)
                        .orElseGet(() -> result.getFieldErrors(field).get(0).getDefaultMessage());
                model.addAttribute(ERR_MSG, errorMessage);
                return "signup";
            }
        }
        return null;
    }
}
