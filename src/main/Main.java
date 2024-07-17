package main;

import javax.swing.JFrame;

public class Main{
	
    public static void main(String[] args) {
    	
    	JFrame window = new JFrame("Simple Chess");
    	 window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	 window.setResizable(false);
    	 
    	 //ADD GAMEPANEL TO WINDOW
    	 gamePanel gp = new gamePanel();
    	 window.add(gp);
    	 window.pack();
    	 
    	 window.setLocationRelativeTo(null);
    	 window.setVisible(true);
    	 
    	 gp.launchGame();
    	 
    }
}