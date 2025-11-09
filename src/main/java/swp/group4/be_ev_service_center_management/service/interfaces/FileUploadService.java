package swp.group4.be_ev_service_center_management.service.interfaces;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

@Service
public class FileUploadService {

    @Autowired
    private Cloudinary cloudinary;

    /**
     * Upload ảnh base64 lên Cloudinary
     * @param base64Image Chuỗi base64 (bao gồm cả prefix "data:image/jpeg;base64,")
     * @return URL của ảnh đã upload
     * @throws IOException
     */
    public String uploadImage(String base64Image) throws IOException {
        if (base64Image == null || base64Image.isEmpty()) {
            return null; // Không có ảnh để upload
        }

        // Cloudinary SDK đủ thông minh để xử lý toàn bộ chuỗi base64 data URI
        Map uploadResult = cloudinary.uploader().upload(base64Image,
                ObjectUtils.asMap(
                        "resource_type", "image", // Chỉ định đây là file ảnh
                        "folder", "ev-service/vehicles" // (Tùy chọn) Sắp xếp ảnh vào thư mục
                ));

        // Lấy URL an toàn (https)
        return uploadResult.get("secure_url").toString();
    }
}
