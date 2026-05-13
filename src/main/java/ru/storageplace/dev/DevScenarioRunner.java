package ru.storageplace.dev;

import ru.storageplace.model.OperationStatus;
import ru.storageplace.model.OperationType;
import ru.storageplace.model.Product;
import ru.storageplace.model.StorageOperation;
import ru.storageplace.model.StoragePlace;
import ru.storageplace.repository.ProductRepository;
import ru.storageplace.repository.StorageBalanceRepository;
import ru.storageplace.repository.StorageOperationRepository;
import ru.storageplace.repository.StoragePlaceRepository;
import ru.storageplace.repository.StoragePlaceStateRepository;
import ru.storageplace.service.StorageOperationService;

public class DevScenarioRunner {
    private final ProductRepository productRepository = new ProductRepository();
    private final StoragePlaceRepository storagePlaceRepository = new StoragePlaceRepository();
    private final StorageBalanceRepository storageBalanceRepository = new StorageBalanceRepository();
    private final StoragePlaceStateRepository storagePlaceStateRepository = new StoragePlaceStateRepository();
    private final StorageOperationRepository storageOperationRepository = new StorageOperationRepository();

    private final StorageOperationService storageOperationService = new StorageOperationService();

    public void runIncomeScenario() {
        Product product = findDemoProduct();
        StoragePlace targetPlace = findStoragePlaceByNumber("1");

        StorageOperation operation = storageOperationService.calculateAndSave(
                OperationType.INCOME,
                product.getId(),
                null,
                targetPlace.getId(),
                10
        );

        printCreatedOperation(operation);

        if (operation.getStatus() == OperationStatus.CALCULATED) {
            storageOperationService.confirmOperation(operation.getId());
            System.out.println("Операция выполнена");
        }

        printDatabaseState();
    }

    public void runOutcomeScenario() {
        Product product = findDemoProduct();
        StoragePlace sourcePlace = findStoragePlaceByNumber("1");

        StorageOperation operation = storageOperationService.calculateAndSave(
                OperationType.OUTCOME,
                product.getId(),
                sourcePlace.getId(),
                null,
                10
        );

        printCreatedOperation(operation);

        if (operation.getStatus() == OperationStatus.CALCULATED) {
            storageOperationService.confirmOperation(operation.getId());
            System.out.println("Операция выполнена");
        }

        printDatabaseState();
    }

    public void runTransferScenario() {
        Product product = findDemoProduct();
        StoragePlace sourcePlace = findStoragePlaceByNumber("1");
        StoragePlace targetPlace = findStoragePlaceByNumber("2");

        StorageOperation operation = storageOperationService.calculateAndSave(
                OperationType.TRANSFER,
                product.getId(),
                sourcePlace.getId(),
                targetPlace.getId(),
                10
        );

        printCreatedOperation(operation);

        if (operation.getStatus() == OperationStatus.CALCULATED) {
            storageOperationService.confirmOperation(operation.getId());
            System.out.println("Операция выполнена");
        }

        printDatabaseState();
    }

    private Product findDemoProduct() {
        return productRepository.findAll()
                .stream()
                .filter(item -> "BOX-001".equals(item.getArticle()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Демонстрационный товар BOX-001 не найден"));
    }

    private StoragePlace findStoragePlaceByNumber(String number) {
        return storagePlaceRepository.findAll()
                .stream()
                .filter(place -> number.equals(place.getNumber()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Демонстрационное место хранения с номером " + number + " не найдено"));
    }

    private void printCreatedOperation(StorageOperation operation) {
        System.out.println("Создана операция:");
        System.out.println("ID: " + operation.getId());
        System.out.println("Тип: " + operation.getOperationType().getDisplayName());
        System.out.println("Статус: " + operation.getStatus().getDisplayName());
        System.out.println("Сообщение: " + operation.getResultMessage());
    }

    private void printDatabaseState() {
        System.out.println();
        System.out.println("Остатки:");
        storageBalanceRepository.findAll().forEach(balance -> System.out.println(
                "placeId=" + balance.getStoragePlaceId()
                        + ", productId=" + balance.getProductId()
                        + ", quantity=" + balance.getQuantity()
                        + ", totalVolume=" + balance.getTotalVolume()
                        + ", totalWeightKg=" + balance.getTotalWeightKg()
        ));

        System.out.println();
        System.out.println("Состояния мест хранения:");
        storagePlaceStateRepository.findAll().forEach(state -> System.out.println(
                "placeId=" + state.getStoragePlaceId()
                        + ", occupiedVolume=" + state.getOccupiedVolume()
                        + ", occupiedWeightKg=" + state.getOccupiedWeightKg()
                        + ", status=" + state.getStatus().getDisplayName()
        ));

        System.out.println();
        System.out.println("Операции:");
        storageOperationRepository.findAll().forEach(item -> System.out.println(
                "id=" + item.getId()
                        + ", type=" + item.getOperationType().getDisplayName()
                        + ", status=" + item.getStatus().getDisplayName()
                        + ", message=" + item.getResultMessage()
        ));
    }
}