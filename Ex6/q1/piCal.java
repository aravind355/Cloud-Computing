import java.rmi.Remote;
import java.rmi.RemoteException;

public interface piCal extends Remote {
    double calculatePi(int not, long ppt) throws RemoteException;
}