import java.util.ArrayList;
import java.util.List;

public class ObserverPattern {

    interface Observer {
        void update(String message);
    }


    class EmailNotification implements Observer {

        @Override
        public void update(String message) {
            System.out.printf("Message received %s%n", message);
        }
    }

    class SmsNotification implements Observer {

        @Override
        public void update(String message) {
            System.out.printf("Message received %s%n", message);
        }
    }

    interface Subject {
        void register(Observer o);

        void unregister(Observer o);

        void notifyObservers();

    }

    class NotificationService implements Subject {
        List<Observer> observers = new ArrayList<>();

        @Override
        public void register(Observer o) {
            observers.add(o);

        }

        @Override
        public void unregister(Observer o) {
            observers.remove(o);

        }

        @Override
        public void notifyObservers() {
            for (Observer observer : observers) {
                observer.update("new message received");
            }
        }
    }
}

