package br.ufrj.dcc.felipealmeida.fowl.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Dictionary;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.ufrj.dcc.felipealmeida.fowl.Fowl;

/**
 * Servlet implementation class Consulta
 */
public class Consulta extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Consulta() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub

		String url = request.getParameter("url").replace("%3F", "?");

		response.setContentType("text/html");
		response.setCharacterEncoding("UTF-8");

		PrintWriter out = response.getWriter();

		if (!url.equalsIgnoreCase("null")) {

			try {
				Dictionary<String, String> conhecimento = Fowl.obterConhecimento(url);
				
				out.println(String.format("<div id=\"label\">%s</div>", conhecimento.get("label")));
				out.println(String.format("<div id=\"abst\">%s</div>", conhecimento.get("abstract")));
				
			} catch (Exception e) {
				out.println(e.getMessage());
			}
		} else {
			out.println("Não foi possível analisar esta página");
		}

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
