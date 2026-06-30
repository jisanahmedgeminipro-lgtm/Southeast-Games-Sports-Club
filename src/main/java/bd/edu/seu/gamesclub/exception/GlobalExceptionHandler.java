package bd.edu.seu.gamesclub.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

/**
 * Centralized MVC exception handling. Maps domain exceptions to the branded
 * error pages, while form-level business errors are typically caught in the
 * controllers themselves and surfaced as flash messages.
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    /** Missing resources render the 404 page. */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ModelAndView handleNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        log.warn("404 for {}: {}", request.getRequestURI(), ex.getMessage());
        ModelAndView mav = new ModelAndView("error/404");
        mav.addObject("errorMessage", ex.getMessage());
        mav.setStatus(HttpStatus.NOT_FOUND);
        return mav;
    }

    /**
     * Any remaining business-rule violation that was not handled by a controller
     * renders the 500 page (with the message available to the view).
     */
    @ExceptionHandler(BusinessException.class)
    public ModelAndView handleBusiness(BusinessException ex, HttpServletRequest request) {
        log.warn("Business error for {}: {}", request.getRequestURI(), ex.getMessage());
        ModelAndView mav = new ModelAndView("error/500");
        mav.addObject("errorMessage", ex.getMessage());
        mav.setStatus(HttpStatus.BAD_REQUEST);
        return mav;
    }

    /** Unexpected failures render the 500 page. */
    @ExceptionHandler(Exception.class)
    public ModelAndView handleUnexpected(Exception ex, HttpServletRequest request) {
        log.error("Unhandled error for {}", request.getRequestURI(), ex);
        // Walk to the root cause so the (non-production) error page can show the
        // real reason - e.g. "Column 'xyz' cannot be null" - instead of the
        // generic "Could not commit JPA transaction" wrapper.
        Throwable root = ex;
        while (root.getCause() != null && root.getCause() != root) {
            root = root.getCause();
        }
        ModelAndView mav = new ModelAndView("error/500");
        mav.addObject("errorDetail", ex.getClass().getSimpleName() + ": " + ex.getMessage());
        if (root != ex) {
            mav.addObject("errorRootCause", root.getClass().getSimpleName() + ": " + root.getMessage());
        }
        mav.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        return mav;
    }
}
