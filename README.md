# Mô tả cơ sở dữ liệu (Firebase)

### Bảng 1: Bảng dữ liệu thể loại

| STT | Tên trường      | Kiểu dữ liệu | Mô tả                                |
|-----|-----------------|--------------|--------------------------------------|
| 1   | Category_id     | String       | Id của thể loại                      |
| 2   | Category_layer  | int          | Phân loại thể loại (cha)            |
| 3   | Category_order  | int          | Thứ tự sắp xếp các thể loại trong danh mục |
| 4   | Category_title  | String       | Tiêu đề các thể loại trong danh mục |

### Bảng 2: Bảng dữ liệu thể loại con (categorieschild)

| STT | Tên trường      | Kiểu dữ liệu | Mô tả                                |
|-----|-----------------|--------------|--------------------------------------|
| 1   | Category_id     | String       | Id của thể loại                      |
| 2   | Category_layer  | int          | Phân loại thể loại (con)            |
| 3   | Category_order  | int          | Thứ tự sắp xếp các thể loại trong danh mục |
| 4   | Category_title  | String       | Tiêu đề các thể loại trong danh mục |

### Bảng 3: Bảng dữ liệu khóa học

| STT | Tên trường          | Kiểu dữ liệu | Mô tả                                          |
|-----|---------------------|--------------|------------------------------------------------|
| 1   | Course_id           | String       | ID khoá học                                    |
| 2   | Heading_id          | String       | ID chương trong khoá học                       |
| 3   | Heading_title       | String       | Tên chương trong khoá học                      |
| 4   | Heading_description | String       | Mô tả chương trong khoá học                    |
| 5   | Heading_order       | Number       | Thứ tự sắp xếp các chương trong khoá học      |

### Bảng 4: Bảng dữ liệu chương trong khóa học

| STT | Tên trường       | Kiểu dữ liệu | Mô tả                  |
|-----|------------------|--------------|------------------------|
| 1   | Course_id        | String       | ID khoá học            |
| 2   | Category_id      | String       | ID thể loại cha       |
| 3   | Category_child_id| String       | ID thể loại con       |

### Bảng 5: Bảng dữ liệu phân loại khóa học

| STT | Tên trường      | Kiểu dữ liệu | Mô tả                       |
|-----|-----------------|--------------|-----------------------------|
| 1   | Course_id       | String       | ID khoá học                 |
| 2   | Comment_id      | String       | ID bình luận                |
| 3   | Comment_content | String       | Nội dung bình luận          |
| 4   | Comment_like    | int          | Lượt thích bình luận        |
| 5   | Comment_rate    | Int          | Đánh giá bình luận          |
| 6   | Comment_upload_time | Date     | Thời gian bình luận         |
| 7   | User_id         | String       | ID người bình luận          |
| 8   | User_avatar     | String       | Ảnh đại diện người bình luận|
| 9   | User_name       | String       | Tên người bình luận         |

### Bảng 6: Bảng dữ liệu bình luận trong khóa học

| STT | Tên trường      | Kiểu dữ liệu | Mô tả             |
|-----|-----------------|--------------|-------------------|
| 1   | Video_id        | String       | ID video          |
| 2   | Video_Title     | String       | Tiêu đề video     |
| 3   | Video_url       | String       | Link video        |

### Bảng 7: Bảng dữ liệu video khóa học

| STT | Tên trường      | Kiểu dữ liệu | Mô tả             |
|-----|-----------------|--------------|-------------------|
| 1   | Document_title  | String       | Tiêu đề tài liệu  |
| 2   | Document_url    | String       | Link tài liệu     |

### Bảng 8: Bảng dữ liệu tài liệu khóa học

| STT | Tên trường      | Kiểu dữ liệu | Mô tả                   |
|-----|-----------------|--------------|-------------------------|
| 1   | User_uid        | String       | Id người dùng           |
| 2   | User_name       | String       | Tên người dùng          |
| 3   | User_job        | String       | Công việc hiện tại người dùng |
| 4   | User_email      | String       | Email người dùng        |
| 5   | User_avatar     | String       | Avatar người dùng       |
| 6   | User_permission | String       | Quyền người dùng       |

### Bảng 9: Bảng dữ liệu người dùng

| STT | Tên trường      | Kiểu dữ liệu | Mô tả             |
|-----|-----------------|--------------|-------------------|
| 1   | User_uid        | String       | Id người dùng     |
| 2   | Video_id        | String       | Id video khoá học |

### Bảng 10: Bảng dữ liệu Checkvideo người dùng

| STT | Tên trường      | Kiểu dữ liệu | Mô tả             |
|-----|-----------------|--------------|-------------------|
| 1   | Course_id       | String       | Id khoá học       |
| 2   | Comment_id      | String       | Id bình luận      |

### Bảng 11: Bảng dữ liệu Checklike người dùng

# Demo sản phẩm

