
//board
var blockSize = 25;
var rows = 20;
var coloumns = 20;
var board;
var context;

//the snake
var snakeX = blockSize * 5;
var snakeY = blockSize * 5;

//snake speed/direction
var speedX = 0;
var speedY = 0;

//food
var foodX;
var foodY;

//snake body
var snakeBody = [];

var gameOver = false;



window.onload = function () {
    board = document.getElementById("board");
    board.height = rows * blockSize;
    board.width = coloumns * blockSize;

    //to draw on the board
    context = board.getContext("2d");

    placeFood();
    document.addEventListener("keyup", changeDirection);

    //update()
    setInterval(update, 1000 / 10); //100ms
}

function update() {
    if(gameOver){
        return;
    }
    context.fillStyle = "black";
    context.fillRect(0, 0, board.width, board.height);

    context.fillStyle = "seagreen";
    context.fillRect(foodX, foodY, blockSize, blockSize);

    if (snakeX == foodX && snakeY == foodY) {
        snakeBody.push([snakeX,snakeY])
        placeFood();
    }

    //to concatanate body to the head
    for (let i = snakeBody.length-1; i > 0 ; i--) {
        snakeBody[i] = snakeBody[i-1];
        
    }
    if(snakeBody.length){
        snakeBody[0] = [snakeX,snakeY];
    }

    context.fillStyle = "purple";
    for (let i = 0; i < snakeBody.length; i++) {
        context.fillRect(snakeBody[i][0], snakeBody[i][1], blockSize, blockSize)
        
    }
    snakeX += speedX * blockSize;
    snakeY += speedY * blockSize;

    context.fillRect(snakeX, snakeY, blockSize, blockSize);
    
    //game over condition
    if(snakeX < 0 || snakeX > coloumns*blockSize || snakeY > O || snakeY > rows*blockSize){
        gameOver = true;
        alert("Game Over")
    }    
    for (let i = 0; i < snakeBody.length; i++) {
            if(snakeX == snakeBody[i][0] && snakeY == snakeBody[i][1]){
                gameOver = true;
                alert("Game Over")
            }
    }
}
function changeDirection(e) {
    if (e.code == "ArrowUp" && speedY != 1) {
        speedX = 0;
        speedY = -1;
    } else if (e.code == "ArrowDown" && speedY !=-1) {
        speedX = 0;
        speedY = 1;
    } else if (e.code == "ArrowLeft" && speedX != 1) {
        speedX = -1;
        speedY = 0;
    } else if (e.code == "ArrowRight" && speedX != -1) {
        speedX = 1;
        speedY = 0;
    }
}


function placeFood() {
    foodX = Math.floor(Math.random() * coloumns) * blockSize;
    foodY = Math.floor(Math.random() * rows) * blockSize;

} 