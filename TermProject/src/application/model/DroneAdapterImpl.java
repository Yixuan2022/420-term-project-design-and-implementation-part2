package application.model;

import java.io.IOException;

import main.java.surelyhuman.jdrone.control.physical.tello.TelloDrone;

public class DroneAdapterImpl implements DroneAdapter{
    private TelloDrone telloDrone;

    public DroneAdapterImpl(TelloDrone drone) {
    	this.telloDrone = drone;
    }

    public void takeoff() throws IOException{
    	telloDrone.takeoff();
    }

	public void activateSDK() throws IOException{
		telloDrone.activateSDK();
	}

	public void streamOn() throws IOException{
		telloDrone.streamOn();
	}
	public void streamViewOn() throws IOException{
		telloDrone.streamViewOn();
	}
	public void hoverInPlace(int front) throws IOException, InterruptedException{
		telloDrone.hoverInPlace(front);
	}
	public void flyForward(int front) throws IOException{
		telloDrone.flyForward(front);
	}
	public void flyLeft(int left) throws IOException{
		telloDrone.flyLeft(left);
	}
	public void flyRight(int right) throws IOException{
		telloDrone.flyRight(right);
	}
	public void turnCW(int degrees) throws IOException{
		telloDrone.turnCW(degrees);
	}
	public void turnCCW(int degrees) throws IOException{
		telloDrone.turnCCW(degrees);
	}
	public void flip(String direction) throws IOException{
		telloDrone.flip(direction);
	}
	public void land() throws IOException{
		telloDrone.land();
	}
	public void streamOff() throws IOException{
		telloDrone.streamOff();
	}
	public void streamViewOff() throws IOException, InterruptedException{
		telloDrone.streamViewOff();
	}
	public void end() throws IOException, InterruptedException{
		telloDrone.end();
	}

	public void gotoXY(int x, int y, int speed) throws IOException{
		telloDrone.gotoXY(x, y, speed);
	}

}
