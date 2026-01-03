public class Adapter {
    class LegacyPaymentService {
        void sendPayment(double amountInDollars) {
            System.out.printf("Payment in dollar amount %s", amountInDollars);
        }
    }

    interface PaymentProcessor {
        void pay(int amountInCents);
    }


    class LegacyPaymentAdapter implements PaymentProcessor {

        final private LegacyPaymentService legacyPaymentService;

        LegacyPaymentAdapter(LegacyPaymentService legacyPaymentService) {
            this.legacyPaymentService = legacyPaymentService;
        }

        @Override
        public void pay(int amountInCents) {
            double amountInDollars = amountInCents / 100.0;
            this.legacyPaymentService.sendPayment(amountInDollars);
        }
    }
}
