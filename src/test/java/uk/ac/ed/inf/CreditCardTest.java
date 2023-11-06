package uk.ac.ed.inf;import static org.junit.Assert.assertFalse;import static org.junit.Assert.assertTrue;import org.junit.Test;import uk.ac.ed.inf.MainOrderValidation;import uk.ac.ed.inf.ilp.data.*;public class CreditCardTest {    /********* Credit Card Information Checks: Number (16 digits), CVV, Exp Date *********///    boolean checkCardNumber = cardNumberCheck(creditCardNumber);//    boolean checkCardExpiry = cardExpiryCheck(creditCardExpiryDate);//    boolean checkCardCvv = cardCvvCheck(creditCvv);    @Test    public void creditCardNumberCheckCorrect() {        MainOrderValidation test = new MainOrderValidation();        String cardNumber = "1234567890123456";        String cardExpiryDate = "12/20";        String cardCvv = "123";        CreditCardInformation creditCardInformation = new CreditCardInformation(cardNumber, cardExpiryDate, cardCvv);        assertTrue(test.cardNumberCheck(creditCardInformation));    }    @Test    public void creditCardNumberMoreThan16CheckIncorrect() {        MainOrderValidation test = new MainOrderValidation();        String cardNumber = "123456789012345";        String cardExpiryDate = "12/20";        String cardCvv = "123";        CreditCardInformation creditCardInformation = new CreditCardInformation(cardNumber, cardExpiryDate, cardCvv);        assertFalse(test.cardNumberCheck(creditCardInformation));    }    @Test    public void creditCardNumberLessThan16CheckIncorrect() {        MainOrderValidation test = new MainOrderValidation();        String cardNumber = "123456789";        String cardExpiryDate = "12/20";        String cardCvv = "123";        CreditCardInformation creditCardInformation = new CreditCardInformation(cardNumber, cardExpiryDate, cardCvv);        assertFalse(test.cardNumberCheck(creditCardInformation));    }    @Test    public void nullCreditCardNumberCheck(){        MainOrderValidation test = new MainOrderValidation();        String cardNumber = null;        String cardExpiryDate = "12/28";        String cardCvv = "123";        CreditCardInformation creditCardInformation = new CreditCardInformation(cardNumber, cardExpiryDate, cardCvv);        assertFalse(test.cardNumberCheck(creditCardInformation));    }    @Test    public void emptyCreditCardNumberCheck(){        MainOrderValidation test = new MainOrderValidation();        String cardNumber = "";        String cardExpiryDate = "12/28";        String cardCvv = "123";        CreditCardInformation creditCardInformation = new CreditCardInformation(cardNumber, cardExpiryDate, cardCvv);        assertFalse(test.cardNumberCheck(creditCardInformation));    }    @Test    public void onlyNumericInCreditCardNumber(){        MainOrderValidation test = new MainOrderValidation();        String cardNumber = "123456789012345TY";        String cardExpiryDate = "12/28";        String cardCvv = "123";        CreditCardInformation creditCardInformation = new CreditCardInformation(cardNumber, cardExpiryDate, cardCvv);        assertFalse(test.cardNumberCheck(creditCardInformation));    }    @Test    public void creditCardExpiryCheckCorrect() {        MainOrderValidation test = new MainOrderValidation();        String cardNumber = "1234567890123456";        String cardExpiryDate = "12/28";        String cardCvv = "123";        CreditCardInformation creditCardInformation = new CreditCardInformation(cardNumber, cardExpiryDate, cardCvv);        assertTrue(test.cardExpiryCheck(creditCardInformation));    }    @Test    public void creditCardExpiryCheckIncorrect() {        MainOrderValidation test = new MainOrderValidation();        String cardNumber = "1234567890123456";        String cardExpiryDate = "12/10";        String cardCvv = "123";        CreditCardInformation creditCardInformation = new CreditCardInformation(cardNumber, cardExpiryDate, cardCvv);        assertFalse(test.cardExpiryCheck(creditCardInformation));    }    @Test    public void creditCardCvvCheckCorrect() {        MainOrderValidation test = new MainOrderValidation();        String cardNumber = "1234567890123456";        String cardExpiryDate = "12/20";        String cardCvv = "123";        CreditCardInformation creditCardInformation = new CreditCardInformation(cardNumber, cardExpiryDate, cardCvv);        assertTrue(test.cardCvvCheck(creditCardInformation));    }    @Test    public void creditCardCvvCheckIncorrect() {        MainOrderValidation test = new MainOrderValidation();        String cardNumber = "1234567890123456";        String cardExpiryDate = "12/20";        String cardCvv = "12";        CreditCardInformation creditCardInformation = new CreditCardInformation(cardNumber, cardExpiryDate, cardCvv);        assertFalse(test.cardCvvCheck(creditCardInformation));    }//   TODO: Test for null, non-numeric, etc values in Exp. Date and CVV}