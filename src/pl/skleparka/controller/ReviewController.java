package pl.skleparka.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import pl.skleparka.beans.Product;
import pl.skleparka.beans.Review;
import pl.skleparka.beans.User;
import pl.skleparka.service.ProductService;
import pl.skleparka.service.ReviewService;
import pl.skleparka.service.UserService;

/**
 * Servlet implementation class ReviewController
 */
@WebServlet("/review")
public class ReviewController extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ReviewController() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	HttpSession session = request.getSession();
    	List<Review> reviews = new ArrayList<Review>();
    	ReviewService reviewService = ReviewService.getInstance();
    	ProductService productService = ProductService.getInstance();
    	UserService userService = UserService.getInstance();
    	
    	int productId = Integer.valueOf(request.getParameter("productId"));
    	Product product = productService.getProduct(productId);
    	List<Product> products = new ArrayList<Product>();
    	products.add(product);
    	List<User> users = new ArrayList<User>();
    	reviews = reviewService.getAllReviewsForProduct(productId);
    	float reviewAvg = 0.0f;
    	
    	
    	for(Review review : reviews) {
    		review.setUsername(userService.getUsernameFromId(review.getUserId()));
    		users.add(UserService.getInstance().getUserById(review.getUserId()));
    		reviewAvg += review.getRating();
    	} 
    	
    	reviewAvg = (float)reviewAvg / (float)reviews.stream().count();
    	session.setAttribute("userList", users);
    	request.setAttribute("userList", users);
    	session.setAttribute("avg", reviewAvg);
    	request.setAttribute("avg", reviewAvg);
		session.setAttribute("review", reviews);
   		request.setAttribute("review", reviews);
   		session.setAttribute("product", products);
   		request.setAttribute("product", products);
   		
		request.getRequestDispatcher("review.jsp").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		HttpSession session = request.getSession();
		String command = request.getParameter("command");
		session.setAttribute("command", command);
		request.setCharacterEncoding("UTF-8");
		int reviewId = Integer.valueOf(request.getParameter("reviewId"));
		if(command == null) command = "editReview";
		switch(command) {
		case "removeReview" : removeReview(request, response, reviewId);
			break;
		default: removeReview(request, response, reviewId);
		}
	}

	private void removeReview(HttpServletRequest request, HttpServletResponse response, int reviewId) throws IOException {
		int productId = ReviewService.getInstance().getReview(reviewId).getProductId();
		ReviewService.getInstance().deleteReview(reviewId);
		response.sendRedirect(request.getContextPath() + "/review?productId=" + productId);
	}

}
