package com.example.dm2.juegosimple;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.PictureDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

public class SimpleGameEngine extends Activity {

    // gameView will be the view of the game
    // It will also hold the logic of the game
    // and respond to screen touches as well
    GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize gameView and set it as the view
        gameView = new GameView(this);
        setContentView(gameView);

    }

    // GameView class will go here

    // Here is our implementation of GameView
    // It is an inner class.
    // Note how the final closing curly brace }
    // is inside SimpleGameEngine

    // Notice we implement runnable so we have
    // A thread and can override the run method.
    class GameView extends SurfaceView implements Runnable {

        // This is our thread
        Thread gameThread = null;

        // This is new. We need a SurfaceHolder
        // When we use Paint and Canvas in a thread
        // We will see it in action in the draw method soon.
        SurfaceHolder ourHolder;

        // A boolean which we will set and unset
        // when the game is running- or not.
        volatile boolean playing;

        // A Canvas and a Paint object
        Canvas canvas;
        Paint paint;

        // This variable tracks the game frame rate
        long fps;

        // This is used to help calculate the fps
        private long timeThisFrame;

        // Declare an object of type Bitmap
        Bitmap bitmapBob,guau,arf,inicio;

        // Bob starts off not moving
        boolean isMoving = false;

        int contador =0;

        // He can walk at 150 pixels per second
        float walkSpeedPerSecond = 600;

        // He starts 10 pixels from the left
        float bobXPosition = 100;

        // El tamaño de la pantalla en pixeles
        int screenX;
        int screenY;

        int arfX=-500,arfY=-500;
        int guauX=-500,guauY=-500;
        int inicioX,inicioY;


        boolean r,facingR=true;

        // Una ball
        Ball ball;




        // When the we initialize (call new()) on gameView
        // This special constructor method runs
        public GameView(Context context) {
            // The next line of code asks the
            // SurfaceView class to set up our object.
            // How kind.
            super(context);

            // Obten un objeto Display para tener acceso a los detalles de la pantalla
            Display display = getWindowManager().getDefaultDisplay();
            // Carga la resolución a un objeto Point
            Point size = new Point();
            display.getSize(size);

            screenX = size.x;
            screenY = size.y;
            inicioX=screenX/2-200;
            inicioY=screenY/2-250;

            // Initialize ourHolder and paint objects
            ourHolder = getHolder();
            paint = new Paint();

            // Load from .png file
            bitmapBob = BitmapFactory.decodeResource(this.getResources(), R.drawable.bob);
            arf = BitmapFactory.decodeResource(this.getResources(), R.drawable.arf);
            guau = BitmapFactory.decodeResource(this.getResources(), R.drawable.guau);
            inicio = BitmapFactory.decodeResource(this.getResources(), R.drawable.inicio);


            // Crea una ball
            ball = new Ball(screenX, screenY);

            // Set our boolean to true - game on!
            playing = true;

        }

        @Override
        public void run() {
            while (playing) {

                // Capture the current time in milliseconds in startFrameTime
                long startFrameTime = System.currentTimeMillis();

                // Update the frame
                update();

                // Draw the frame
                draw();

                // Calculate the fps this frame
                // We can then use the result to
                // time animations and more.
                timeThisFrame = System.currentTimeMillis() - startFrameTime;
                if (timeThisFrame > 0) {
                    fps = 1000 / timeThisFrame;
                }

            }

        }

        // Everything that needs to be updated goes in here
        // In later projects we will have dozens (arrays) of objects.
        // We will also do other things like collision detection.
        public void update() {

            // Rebota la pelota cuando golpea el borde inferior de la pantalla
            if(ball.getRect().bottom > screenY){
                ball.reverseYVelocity();
                ball.clearObstacleY(screenY - 20);
            }
            // Rebota le pelota de vuelta cuando golpee el borde superior de la pantalla
            if(ball.getRect().top < 0){
                ball.reverseYVelocity();
                ball.clearObstacleY(20);
            }
            // Rebota si la ball golpea la pared izquierda
            if(ball.getRect().left < 0){
                ball.reverseXVelocity();
                ball.clearObstacleX(20);
            }
            // Rebota si la ball golpea la pared derecha
            if(ball.getRect().right > screenX){
                ball.reverseXVelocity();
                ball.clearObstacleX(screenX - 20);
            }






            ball.update(fps);
            // If bob is moving (the player is touching the screen)
            // then move him to the right based on his target speed and the current fps.



            //Integer aux = screenY/2-bitmapBob.getHeight()/2;
            //Log.i("Y_RESTA",aux.toString());
            //Float aux2 = ball.getRect().top;
            //Log.i("Y_BALL",aux2.toString());

            //Mensaje cuando sale de la pantalla el perro por los lados
            if(bobXPosition<=0||bobXPosition>=screenX-bitmapBob.getWidth())
            {
                arfX=(screenX/2)-bitmapBob.getWidth();
                arfY=(screenY/2)-bitmapBob.getHeight();

            }
            else{//Quito el mensaje cuando el perro vuelve a la pantalla
                arfX=-500;
                arfY=-500;
            }
                if (isMoving && r) {
                        bobXPosition = bobXPosition + (walkSpeedPerSecond / fps);
                } else if (isMoving && !r) {
                        bobXPosition = bobXPosition - (walkSpeedPerSecond / fps);
                }

        }

        // Draw the newly updated scene
        public void draw() {

            // Make sure our drawing surface is valid or we crash
            if (ourHolder.getSurface().isValid()) {
                // Lock the canvas ready to draw
                canvas = ourHolder.lockCanvas();

                // Draw the background color
                canvas.drawColor(Color.argb(255,  26, 128, 182));

                // Choose the brush color for drawing
                paint.setColor(Color.argb(255,  249, 129, 0));

                // Make the text a bit bigger
                paint.setTextSize(45);

                // Display the current fps on the screen
                canvas.drawText("FPS:" + fps, screenX-150, 40, paint);

                //Instrucciones
                canvas.drawBitmap(inicio, inicioX, inicioY, paint);

                // Draw bob at bobXPosition, 200 pixels
                canvas.drawBitmap(bitmapBob, bobXPosition, 200, paint);

                // Draw la ball
                canvas.drawRect(ball.getRect(), paint);



                canvas.drawBitmap(arf, arfX, arfY, paint);
                canvas.drawBitmap(guau, guauX, guauY, paint);

                // Traza el HUD
                // Elige el color del pincel para dibujar
                paint.setColor(Color.argb(255,  255, 255, 255));

                // Traza el score
                paint.setTextSize(40);
                canvas.drawText("Score: " + contador , 10,50, paint);



                // Draw everything to the screen
                ourHolder.unlockCanvasAndPost(canvas);


            }

        }
        public void restart(){

            // Pon la ball de vuelta en el inicio

            ball.reset((int)Math.round(Math.random()*screenX), (int)Math.round(Math.random()*screenY));

        }

        // If SimpleGameEngine Activity is paused/stopped
        // shutdown our thread.
        public void pause() {
            playing = false;
            try {
                gameThread.join();
            } catch (InterruptedException e) {
                Log.e("Error:", "joining thread");
            }

        }

        // If SimpleGameEngine Activity is started then
        // start our thread.
        public void resume() {
            playing = true;
            gameThread = new Thread(this);
            gameThread.start();
        }

        // The SurfaceView class implements onTouchListener
        // So we can override this method and detect screen touches.
        @Override
        public boolean onTouchEvent(MotionEvent motionEvent) {

            switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {

                // Player has touched the screen
                case MotionEvent.ACTION_DOWN:

                    inicioX=-1000;
                    inicioY=-1000;
                    guauX=-500;
                    guauY=-500;

                    // Set isMoving so Bob is moved in the update method
                    if(motionEvent.getX() > screenX / 2) {
                        if(facingR) {
                            Bitmap bInput/*your input bitmap*/, bOutput;
                            Matrix matrix = new Matrix();
                            matrix.preScale(-1.0f, 1.0f);
                            bitmapBob = Bitmap.createBitmap(bitmapBob, 0, 0, bitmapBob.getWidth(), bitmapBob.getHeight(), matrix, true);
                            facingR=false;
                        }
                        r = true;
                    }
                    else {
                        if(!facingR) {
                            Bitmap bInput/*your input bitmap*/, bOutput;
                            Matrix matrix = new Matrix();
                            matrix.preScale(-1.0f, 1.0f);
                            bitmapBob = Bitmap.createBitmap(bitmapBob, 0, 0, bitmapBob.getWidth(), bitmapBob.getHeight(), matrix, true);
                            facingR=true;
                        }
                        r = false;
                    }

                    isMoving = true;

                    break;

                // Player has removed finger from screen
                case MotionEvent.ACTION_UP:
                    //Al levantar el dedo compruebo si el perro esta en contacto con la bola
                    if(bobXPosition<=ball.getRect().right&&bobXPosition+bitmapBob.getWidth()>=ball.getRect().left&&screenY/2-bitmapBob.getHeight()/2<=ball.getRect().top&&screenY/2+bitmapBob.getHeight()/2>=ball.getRect().bottom)
                    {
                        guauX=(int)bobXPosition+40;
                        guauY=bitmapBob.getHeight()-50;
                        contador++;
                        restart();

                    }

                    //Integer aux = bitmapBob.getWidth();
                    //Log.i("WIDTH-------------",aux.toString());
                    //Float aux2 = bobXPosition;
                    //Log.i("SCREENWIDTH-----------",aux2.toString());
                    // Set isMoving so Bob does not move
                    isMoving = false;

                    break;
            }
            return true;
        }

    }
    // This is the end of our GameView inner class

    // More SimpleGameEngine methods will go here

    // This method executes when the player starts the game
    @Override
    protected void onResume() {
        super.onResume();

        // Tell the gameView resume method to execute
        gameView.resume();
    }

    // This method executes when the player quits the game
    @Override
    protected void onPause() {
        super.onPause();

        // Tell the gameView pause method to execute
        gameView.pause();
    }

}