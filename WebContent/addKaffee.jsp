<%@page import="java.util.ArrayList"%>
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
		String zutaten[] = request.getParameterValues("zutaten");
		List<String> zutatenList = new ArrayList<String>();
	
		if (zutaten != null) {
			for (int i = 0; i < zutaten.length; i++) {
		    	zutatenList.add(zutaten[i]);
			}
		}
		
		String name = request.getParameter("nameInput");
		String herkunft = request.getParameter("herkunftInput");
		
		KaffeeService service = new KaffeeService();
		service.addNewKaffee(name, herkunft, null, zutatenList);
	%>
	<div class="container">
		<div class="row header">
			<div class="col-md-12">
				<h1>Kaffee gespeichert</h1>
			</div>
		</div>
		<div class="row">
			<div class="col-md-12">
				<jsp:include page="menu.html" />
			</div>
			<div class="col-md-12">
			Der Kaffee mit dem Namen <b><%out.write(name);%></b> und den angegebenen Zutaten wurde erfolgreich gespeichert.				
			</div>
		</div>
	</div>
</body>
</html>