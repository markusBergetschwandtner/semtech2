<%@page import="java.util.List"%>
<%@page import="at.jku.semtech.miniprojekt2.services.KaffeeService"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Kaffee-Finder</title>

<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1.0">

<link rel="stylesheet" href="css/bootstrap.min.css">
<link rel="stylesheet" href="css/bootstrap-theme.min.css">
<link rel="stylesheet" href="css/font-awesome.min.css">
<link rel="stylesheet" href="css/bootstrap-ics.css">
<link rel="stylesheet" href="css/style.css">

<script src="http://code.jquery.com/jquery-1.10.1.min.js"></script>
<script src="js/bootstrap.min.js"></script>

</head>
<body>
	<%
	    KaffeeService service = new KaffeeService();
	    List<Object> zutaten = service.bringstDuZutatenAlleBabo();
	%>
	<div class="container">
		<div class="row header">
			<div class="col-md-12">
				<h1>Kaffee suchen</h1>
			</div>
		</div>
		<div class="row">
			<div class="col-md-12">
				<jsp:include page="menu.html" />
			</div>
			<div class="col-md-8">
				<div class="table-responsive">
					<table class="table table-striped table-bordered table-hover">
						<thead>
							<tr>
								<th>Zutat</th>
								<th>auswählen</th>
							</tr>
						</thead>
						<tbody>
							<%
							    for (Object o : zutaten) {
									out.write("<tr>");
									if (o instanceof List<?>) {
	
									    for (String s : (List<String>) o) {
											out.write("<tr>");
											out.write("<td>&nbsp;&nbsp;&nbsp;-&nbsp;&nbsp;" + s + "</td>");
											out.print("<td><input type='checkbox' name='zutaten' value='"
												+ s + "'/></td>");
											out.write("</tr>");
									    }
									} else {
									    out.write("<tr>");
									    out.write("<td><b>" + o.toString() + "</b></td>");
									    out.print("<td><input type='checkbox' name='zutaten' value='"
											+ o.toString() + "'/></td>");
									    out.write("</tr>");
									}
							    }
							%>
						</tbody>
					</table>
				</div>
				<div class="col-md-4">
					
				</div>			
			</div>
		</div>
	</div>
</body>
</html>