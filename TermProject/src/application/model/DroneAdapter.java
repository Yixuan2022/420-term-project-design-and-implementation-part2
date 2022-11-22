package application.model;

import java.io.IOException;

public interface DroneAdapter {
	public void takeoff() throws IOException;
	public void activateSDK() throws IOException;
	public void streamOn() throws IOException;
	public void streamViewOn() throws IOException;
	public void hoverInPlace(int front) throws IOException, InterruptedException;
	public void flyForward(int front) throws IOException;
	public void flyLeft(int left) throws IOException;
	public void flyRight(int right) throws IOException;
	public void turnCW(int degrees) throws IOException;
	public void turnCCW(int degrees) throws IOException;
	public void flip(String direction) throws IOException;
	public void land() throws IOException;
	public void streamOff() throws IOException;
	public void streamViewOff() throws IOException, InterruptedException;
	public void end() throws IOException, InterruptedException;
	public void gotoXY(int x, int y, int speed) throws IOException;

}