import java.io.IOException;
import java.util.Map;
import java.util.Scanner;

public class Strategy {

    interface PaymentStrategy {
        void pay(int amount);
    }

    static class CreditCardPayment implements PaymentStrategy {

        @Override
        public void pay(int amount) {
            System.out.println("Paying with Credit card :: Amount" + amount);
        }
    }

    static class UpiPayment implements PaymentStrategy {

        @Override
        public void pay(int amount) {
            System.out.println("Paying with UPI :: Amount" + amount);
        }
    }

    static class NetBankingPayment implements PaymentStrategy {

        @Override
        public void pay(int amount) {
            System.out.println("Paying with Net Banking :: Amount" + amount);
        }
    }

    static class PaymentFactory {
        final static Map<String, PaymentStrategy> strategyMap = Map.of("UPI", new UpiPayment(), "CREDIT_CARD", new CreditCardPayment(), "NET_BANKING", new NetBankingPayment());


        static PaymentStrategy getPaymentMethod(String type) {
            if (!strategyMap.containsKey(type)) {
                throw new IllegalArgumentException("Invalid payment type: " + type);
            }
            return strategyMap.get(type);
        }
    }

    static class PaymentService {

        public PaymentService() {
        }

        public void process(String type, int amount) {
            PaymentFactory.getPaymentMethod(type).pay(amount);
        }

    }

    public static void main(String[] args) throws IOException {
        Scanner reader = new Scanner(System.in);

        String type = reader.nextLine().trim();
        int amount = reader.nextInt();
        PaymentService service = new PaymentService();
        service.process(type, amount);
    }
}
