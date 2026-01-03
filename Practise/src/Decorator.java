import java.util.Date;

public class Decorator {

    static interface Logger {
        void log(String message);
    }

    static class ConsoleLogger implements Logger {

        @Override
        public void log(String message) {
            System.out.printf("console :%s ", message);
        }
    }

    static abstract class LogDecorator implements Logger {
        final protected Logger logger;

        public LogDecorator(Logger logger) {
            this.logger = logger;
        }

//        @Override
//        public void log(String message) {
//            System.out.println("Before Log : ");
//            this.logger.log(message);
//            System.out.println("After Log : ");
//        }

        //  abstract void logMessage(String message);

    }

    static class TimestampLogger extends LogDecorator {

        public TimestampLogger(Logger logger) {
            super(logger);
        }

        @Override
        public void log(String message) {
            System.out.println("Before timestamp : ");
            super.logger.log("timestamp: " + new Date() + " " + message);
            System.out.println("After timestamp : ");
        }
    }

    static class ErrorLevelLogger extends LogDecorator {

        public ErrorLevelLogger(Logger logger) {
            super(logger);
        }

        @Override
        public void log(String message) {
            System.out.println("Before Error : ");
            super.logger.log("level: error " + message);
            System.out.println("After Error : ");
        }
    }

    static class FileLogger extends LogDecorator {

        public FileLogger(Logger logger) {
            super(logger);
        }

        @Override
        public void log(String message) {
            System.out.println("Before File : ");
            super.logger.log("file: logger " + message);
            System.out.println("After File : ");
        }
    }

    public static void main(String[] args) {
        Logger logger = new TimestampLogger(new ErrorLevelLogger(new FileLogger(new ConsoleLogger())));
        logger.log("hello ji");

    }
}
