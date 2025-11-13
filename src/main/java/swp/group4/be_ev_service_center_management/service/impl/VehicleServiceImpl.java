package swp.group4.be_ev_service_center_management.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import swp.group4.be_ev_service_center_management.dto.request.CreateVehicleRequest;
import swp.group4.be_ev_service_center_management.dto.response.MaintenanceHistoryResponse;
import swp.group4.be_ev_service_center_management.dto.response.MaintenancePackageResponse;
import swp.group4.be_ev_service_center_management.entity.Customer;
import swp.group4.be_ev_service_center_management.entity.MaintenancePackage;
import swp.group4.be_ev_service_center_management.entity.Vehicle;
import swp.group4.be_ev_service_center_management.repository.CustomerRepository;
import swp.group4.be_ev_service_center_management.repository.MaintenancePackageRepository;
import swp.group4.be_ev_service_center_management.repository.MaintenanceScheduleRepository;
import swp.group4.be_ev_service_center_management.repository.VehicleRepository;
import swp.group4.be_ev_service_center_management.service.interfaces.VehicleService;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;
    private final MaintenancePackageRepository maintenancePackageRepository;
    private final MaintenanceScheduleRepository maintenanceScheduleRepository;
    private static final int MILEAGE_INTERVAL = 5000;
    private static final int MILEAGE_THRESHOLD = 200;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private FileUploadServiceImpl fileUploadServiceImpl;

    @Override
    public MaintenancePackageResponse getSuggestedPackage(int vehicleId, int currentMileage) {
        // Logic for getSuggestedPackage remains the same
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new RuntimeException("Vehicle not found with ID: " + vehicleId));
        int nextMilestone = (int) (Math.floor((double) currentMileage / MILEAGE_INTERVAL) + 1) * MILEAGE_INTERVAL;
        int previousMilestone = nextMilestone - MILEAGE_INTERVAL;
        int targetMilestone = 0;
        if (Math.abs(currentMileage - nextMilestone) <= MILEAGE_THRESHOLD) {
            targetMilestone = nextMilestone;
        } else if (currentMileage > previousMilestone && Math.abs(currentMileage - previousMilestone) <= MILEAGE_THRESHOLD) {
            targetMilestone = previousMilestone;
        }
        if (targetMilestone > 0) {
            String packageName = "Bảo dưỡng " + targetMilestone + "km";
            Optional<MaintenancePackage> suggestedPackage = maintenancePackageRepository.findByName(packageName);
            if (suggestedPackage.isPresent()) {
                MaintenancePackage pkg = suggestedPackage.get();
                return MaintenancePackageResponse.builder()
                        .packageId(pkg.getPackageId())
                        .packageName(pkg.getName())
                        .description("Bảo dưỡng định kỳ lần " + (targetMilestone / MILEAGE_INTERVAL))
                        .reason("(Đã chạy " + currentMileage + "km)")
                        .build();
            }
        }
        return null;
    }

    @Override
    public MaintenanceHistoryResponse getMaintenanceHistory(int vehicleId, int currentMileage) {
        // 1. Tính toán cấp bảo dưỡng hiện tại
        int currentMaintenanceLevel = (int) Math.ceil((double) currentMileage / MILEAGE_INTERVAL);

        // 2. Lấy các mốc bảo dưỡng đã hoàn thành
        List<Integer> completedMilestones = maintenanceScheduleRepository.findCompletedMaintenanceMilestonesByVehicleId(vehicleId);
        List<Integer> completedLevels = completedMilestones.stream()
                .map(milestone -> milestone / MILEAGE_INTERVAL)
                .sorted()
                .collect(Collectors.toList());

        // 3. Tìm lần bảo dưỡng tiếp theo cần làm
        int nextRequiredLevel = 1;
        for (int level : completedLevels) {
            if (level == nextRequiredLevel) {
                nextRequiredLevel++;
            }
        }

        // 4. Kiểm tra quá hạn
        boolean isOverdue = currentMaintenanceLevel > nextRequiredLevel;
        String overdueMessage = isOverdue ? "Quá hạn lần " + nextRequiredLevel : null;

        return MaintenanceHistoryResponse.builder()
                .completedMaintenances(completedLevels)
                .nextRequiredMaintenance(nextRequiredLevel)
                .currentMaintenanceLevel(currentMaintenanceLevel)
                .isOverdue(isOverdue)
                .overdueMessage(overdueMessage)
                .build();
    }

    public Vehicle createVehicle(CreateVehicleRequest request) {

        try {
            // === PHẦN XỬ LÝ ẢNH ===
            String finalImageUrl = null;
            if (request.getImageUrl() != null && !request.getImageUrl().isEmpty()) {
                // 1. Lấy chuỗi base64 từ request
                String base64Image = request.getImageUrl();

                // 2. Upload lên Cloudinary và lấy URL về
                finalImageUrl = fileUploadServiceImpl.uploadImage(base64Image);
            }
            // ======================

            // 3. Tìm khách hàng
            Customer customer = customerRepository.findById(request.getCustomerId())
                    .orElseThrow(() -> new RuntimeException("Customer not found with id: " + request.getCustomerId()));

            // 4. Tạo đối tượng Vehicle mới
            Vehicle newVehicle = new Vehicle();
            newVehicle.setCustomer(customer);
            newVehicle.setLicensePlate(request.getLicensePlate());
            newVehicle.setModel(request.getModel());
            newVehicle.setVin(request.getVin());
            newVehicle.setCurrentMileage(request.getCurrentMileage());
            newVehicle.setLastServiceDate(request.getLastServiceDate() != null ? LocalDate.parse(request.getLastServiceDate()) : null);

            // 5. QUAN TRỌNG: Lưu URL từ Cloudinary vào DB
            newVehicle.setImageUrl(finalImageUrl); // <-- Sửa ở đây

            // 6. Lưu vehicle vào database
            return vehicleRepository.save(newVehicle);

        } catch (IOException e) {
            // Xử lý nếu upload ảnh thất bại
            throw new RuntimeException("Lỗi khi upload ảnh: " + e.getMessage(), e);
        } catch (Exception e) {
            // Xử lý các lỗi khác
            throw new RuntimeException("Không thể tạo xe: " + e.getMessage(), e);
        }
    }
}
