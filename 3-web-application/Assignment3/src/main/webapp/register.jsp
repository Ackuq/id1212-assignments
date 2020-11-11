<%@page contentType="text/html" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Login / Register</title>
    </head>
    <body>
        <h1>Pick username</h1>
        <h1>${message}</h1>
        <form method="POST" action="/Assignment3/register">
            <input name="username" placeholder="Username" />
            <button type="submit">Submit</button>
        </form>
    </body>
</html>
