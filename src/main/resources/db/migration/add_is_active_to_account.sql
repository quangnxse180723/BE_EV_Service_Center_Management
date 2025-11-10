-- Thêm column is_active vào bảng account để quản lý trạng thái khóa/mở khóa
-- Mặc định tất cả tài khoản hiện tại là hoạt động (is_active = true)

ALTER TABLE account 
ADD COLUMN is_active BOOLEAN NOT NULL DEFAULT TRUE 
COMMENT 'Trạng thái tài khoản: true = hoạt động, false = bị khóa';

-- Update tất cả tài khoản hiện có thành hoạt động
UPDATE account SET is_active = TRUE WHERE is_active IS NULL;
