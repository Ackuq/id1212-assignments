<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Done</title>
    </head>
    <body>
        <h1>You are done! Congrats you got ${correct} correct!</h1>
        <form method="POST" action="/Assignment3/reset-quiz">
            <button type="submit">Reset quiz</button>
        </form>
    </body>
</html>
