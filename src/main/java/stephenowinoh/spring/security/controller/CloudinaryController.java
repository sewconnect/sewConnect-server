package stephenowinoh.spring.security.controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/cloudinary")
@CrossOrigin(origins = "*")
public class CloudinaryController {

    @Autowired
    private Cloudinary cloudinary;

    @GetMapping("/signature")
    public ResponseEntity<Map<String, String>> getSignature(
            @RequestParam(required = false) String folder) {
        try {
            long timestamp = System.currentTimeMillis() / 1000;

            Map<String, Object> params = new HashMap<>();
            params.put("timestamp", timestamp);

            // Include folder in signature if provided
            if (folder != null && !folder.isEmpty()) {
                params.put("folder", folder);
            }

            String signature = cloudinary.apiSignRequest(params, cloudinary.config.apiSecret);

            System.out.println("=== CLOUDINARY SIGNATURE DEBUG ===");
            System.out.println("Cloud Name: " + cloudinary.config.cloudName);
            System.out.println("API Key: " + cloudinary.config.apiKey);
            System.out.println("API Secret (first 4 chars): " + cloudinary.config.apiSecret.substring(0, 4) + "...");
            System.out.println("Folder: " + (folder != null ? folder : "none"));
            System.out.println("Timestamp: " + timestamp);
            System.out.println("Signature: " + signature);
            System.out.println("================================");

            Map<String, String> response = new HashMap<>();
            response.put("signature", signature);
            response.put("timestamp", String.valueOf(timestamp));
            response.put("cloudName", cloudinary.config.cloudName);
            response.put("apiKey", cloudinary.config.apiKey);
            if (folder != null) {
                response.put("folder", folder);
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("ERROR generating signature: " + e.getMessage());
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/verify-upload")
    public ResponseEntity<?> verifyUpload(@RequestBody Map<String, String> payload) {
        try {
            String publicId = payload.get("public_id");
            String version = payload.get("version");
            String signature = payload.get("signature");

            Map<String, Object> params = new HashMap<>();
            params.put("public_id", publicId);
            params.put("version", version);

            String expectedSignature = cloudinary.apiSignRequest(params, cloudinary.config.apiSecret);

            if (expectedSignature.equals(signature)) {
                // Generate URL with proper version
                String imageUrl = cloudinary.url()
                        .secure(true)
                        .version(version)
                        .generate(publicId);

                System.out.println("=== VERIFY UPLOAD DEBUG ===");
                System.out.println("Public ID: " + publicId);
                System.out.println("Version: " + version);
                System.out.println("Generated URL: " + imageUrl);
                System.out.println("===========================");

                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("url", imageUrl);
                response.put("public_id", publicId);

                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "Invalid signature");
                return ResponseEntity.badRequest().body(error);
            }
        } catch (Exception e) {
            System.err.println("ERROR verifying upload: " + e.getMessage());
            e.printStackTrace();
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @DeleteMapping("/delete/{publicId}")
    public ResponseEntity<?> deleteImage(@PathVariable String publicId) {
        try {
            Map result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}