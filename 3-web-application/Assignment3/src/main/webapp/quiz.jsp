<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Quiz</title>
    </head>
    <body style="text-align: center">
        <h1>Question ${current}</h1>
        <h4>${question}</h4>
        <form method="POST" action="/Assignment3/quiz" style="display: flex; flex-direction: column; align-items:  center">
            <div>
                <input id="a" type="radio" name="answer" value="1"  />
                <label for="a">${a}</label>
            </div>
            <div>
                <input id="b" type="radio" name="answer" value="2" />
                <label for="b">${b}</label>
            </div>
            <div>
                <input id="c" type="radio" name="answer" value="3" />
                <label for="c">${c}</label>
            </div>
            <div>
                <input id="d" type="radio" name="answer" value="4" />
                <label for="d">${d}</label>
            </div>
            <br/>
            <button type="submit" style="width: 100px">Submit</button>
        </form>
    </body>
</html>
