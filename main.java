import java.util.*;

//1. Декоратор паттерні (Reports)


interface IReport {
    String generate();
}


class SalesReport implements IReport {
    @Override
    public String generate() {
        return "Sales Report:\n1. Order #101 | Amount: 12000 | Date: 2025-10-20\n2. Order #102 | Amount: 8500 | Date: 2025-10-25\n";
    }
}

class UserReport implements IReport {
    @Override
    public String generate() {
        return "User Report:\n1. User: Aigerim | Orders: 5 | Last Login: 2025-10-27\n2. User: Nursultan | Orders: 2 | Last Login: 2025-10-28\n";
    }
}


abstract class ReportDecorator implements IReport {
    protected IReport report;

    public ReportDecorator(IReport report) {
        this.report = report;
    }

    public String generate() {
        return report.generate();
    }
}


class DateFilterDecorator extends ReportDecorator {
    private String startDate;
    private String endDate;

    public DateFilterDecorator(IReport report, String startDate, String endDate) {
        super(report);
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Override
    public String generate() {
        return report.generate() + "[Date Filter applied: from " + startDate + " to " + endDate + "]\n";
    }
}

class SortingDecorator extends ReportDecorator {
    private String criteria;

    public SortingDecorator(IReport report, String criteria) {
        super(report);
        this.criteria = criteria;
    }

    @Override
    public String generate() {
        return report.generate() + "[Sorted by: " + criteria + "]\n";
    }
}

class CsvExportDecorator extends ReportDecorator {
    public CsvExportDecorator(IReport report) {
        super(report);
    }

    @Override
    public String generate() {
        return report.generate() + "[Exported as CSV file]\n";
    }
}

class PdfExportDecorator extends ReportDecorator {
    public PdfExportDecorator(IReport report) {
        super(report);
    }

    @Override
    public String generate() {
        return report.generate() + "[Exported as PDF file]\n";
    }
}

//  2. Адаптер паттерні (Logistics System) 


interface IInternalDeliveryService {
    void deliverOrder(String orderId);
    String getDeliveryStatus(String orderId);
}

class InternalDeliveryService implements IInternalDeliveryService {
    private Map<String, String> orders = new HashMap<>();

    public void deliverOrder(String orderId) {
        orders.put(orderId, "Delivered via Internal Service");
        System.out.println("Internal delivery started for order " + orderId);
    }

    public String getDeliveryStatus(String orderId) {
        return orders.getOrDefault(orderId, "No such order");
    }
}


class ExternalLogisticsServiceA {
    public void shipItem(int itemId) {
        System.out.println("External A shipping item #" + itemId);
    }

    public String trackShipment(int shipmentId) {
        return "External A tracking shipment #" + shipmentId + ": In transit";
    }
}


class ExternalLogisticsServiceB {
    public void sendPackage(String packageInfo) {
        System.out.println("External B sending package: " + packageInfo);
    }

    public String checkPackageStatus(String trackingCode) {
        return "External B package " + trackingCode + " delivered successfully.";
    }
}


class LogisticsAdapterA implements IInternalDeliveryService {
    private ExternalLogisticsServiceA serviceA;

    public LogisticsAdapterA(ExternalLogisticsServiceA serviceA) {
        this.serviceA = serviceA;
    }

    public void deliverOrder(String orderId) {
        int id = Integer.parseInt(orderId.replaceAll("\\D", ""));
        serviceA.shipItem(id);
    }

    public String getDeliveryStatus(String orderId) {
        int id = Integer.parseInt(orderId.replaceAll("\\D", ""));
        return serviceA.trackShipment(id);
    }
}


class LogisticsAdapterB implements IInternalDeliveryService {
    private ExternalLogisticsServiceB serviceB;

    public LogisticsAdapterB(ExternalLogisticsServiceB serviceB) {
        this.serviceB = serviceB;
    }

    public void deliverOrder(String orderId) {
        serviceB.sendPackage("Order ID: " + orderId);
    }

    public String getDeliveryStatus(String orderId) {
        return serviceB.checkPackageStatus(orderId);
    }
}


class DeliveryServiceFactory {
    public static IInternalDeliveryService getService(String type) {
        switch (type.toLowerCase()) {
            case "internal":
                return new InternalDeliveryService();
            case "external_a":
                return new LogisticsAdapterA(new ExternalLogisticsServiceA());
            case "external_b":
                return new LogisticsAdapterB(new ExternalLogisticsServiceB());
            default:
                throw new IllegalArgumentException("Unknown delivery type: " + type);
        }
    }
}


public class Main {
    public static void main(String[] args) {
        System.out.println("ДЕКОРАТОР ПАТТЕРН (REPORTS)");

        IReport report = new SalesReport();
        report = new DateFilterDecorator(report, "2025-10-01", "2025-10-28");
        report = new SortingDecorator(report, "Amount");
        report = new CsvExportDecorator(report);
        report = new PdfExportDecorator(report);

        System.out.println(report.generate());

        System.out.println("\nАДАПТЕР ПАТТЕРН (DELIVERY)");

        IInternalDeliveryService service1 = DeliveryServiceFactory.getService("internal");
        service1.deliverOrder("O123");
        System.out.println(service1.getDeliveryStatus("O123"));

        IInternalDeliveryService service2 = DeliveryServiceFactory.getService("external_a");
        service2.deliverOrder("O456");
        System.out.println(service2.getDeliveryStatus("O456"));

        IInternalDeliveryService service3 = DeliveryServiceFactory.getService("external_b");
        service3.deliverOrder("O789");
        System.out.println(service3.getDeliveryStatus("O789"));
    }
}
