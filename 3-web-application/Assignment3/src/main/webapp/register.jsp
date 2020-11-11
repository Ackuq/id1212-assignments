<%-- 
    Document   : login
    Created on : Nov 6, 2020, 9:42:50 AM
    Author     : axel
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Login / Register</title>
    </head>
    <body>
        <h1>Pick username</h1>
        <h1><%=request.getParameter("message")%></h1>
        <form method="POST" action="/Assignment3/login">
            <input name="email" placeholder="E-mail" type="email" />
            <input name="password" placeholder="Password" type="password" />
            <button type="submit">Submit</button>
        </form>
    </body>
</html>
