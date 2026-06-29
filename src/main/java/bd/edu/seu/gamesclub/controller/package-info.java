/**
 * Presentation layer (Spring MVC controllers).
 *
 * <p>Controllers translate HTTP requests into service calls and select the
 * Thymeleaf view (or redirect) to render. They contain no business logic and
 * never touch repositories directly - they delegate to the service layer,
 * keeping responsibilities cleanly separated (SOLID / MVC).
 */
package bd.edu.seu.gamesclub.controller;
