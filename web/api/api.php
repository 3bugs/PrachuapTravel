<?php
session_start();
require_once 'global.php';

/*define('PLACE_TYPE_TOUR', 'TOUR');
define('PLACE_TYPE_TEMPLE', 'TEMPLE');
define('PLACE_TYPE_RESTAURANT', 'RESTAURANT');
define('PLACE_TYPE_OTOP', 'OTOP');*/

error_reporting(E_ERROR | E_PARSE);
header('Content-type: application/json; charset=utf-8');

header('Expires: Sun, 01 Jan 2014 00:00:00 GMT');
header('Cache-Control: no-store, no-cache, must-revalidate');
header('Cache-Control: post-check=0, pre-check=0', FALSE);
header('Pragma: no-cache');

$response = array();

$request = explode('/', trim($_SERVER['PATH_INFO'], '/'));
$action = strtolower(array_shift($request));
$id = array_shift($request);

require_once 'db_config.php';
$db = new mysqli(DB_SERVER, DB_USER, DB_PASSWORD, DB_DATABASE);

if ($db->connect_errno) {
    $response[KEY_ERROR_CODE] = ERROR_CODE_ERROR;
    $response[KEY_ERROR_MESSAGE] = 'เกิดข้อผิดพลาดในการเชื่อมต่อฐานข้อมูล';
    $response[KEY_ERROR_MESSAGE_MORE] = $db->connect_error;
    echo json_encode($response);
    exit();
}
$db->set_charset("utf8");

//sleep(1); //todo:

switch ($action) {
    case 'get_place':
        doGetPlace();
        break;
    case 'update_place':
        doUpdatePlace();
        break;
    case 'delete_place_image':
        doDeletePlaceImage();
        break;
    case 'add_nearby':
        doAddNearby();
        break;
    case 'update_nearby':
        doUpdateNearby();
        break;
    case 'delete_nearby':
        doDeleteNearby();
        break;
    case 'delete_nearby_image':
        doDeleteNearbyImage();
        break;
    default:
        $response[KEY_ERROR_CODE] = ERROR_CODE_ERROR;
        $response[KEY_ERROR_MESSAGE] = 'No action specified or invalid action.';
        $response[KEY_ERROR_MESSAGE_MORE] = '';
        break;
}

$db->close();
echo json_encode($response);
exit();

function doGetPlace()
{
    global $db, $response;

    $sql = "SELECT * FROM prachuap_place";
    if ($result = $db->query($sql)) {
        $response[KEY_ERROR_CODE] = ERROR_CODE_SUCCESS;
        $response[KEY_ERROR_MESSAGE] = 'อ่านข้อมูลสำเร็จ';
        $response[KEY_ERROR_MESSAGE_MORE] = '';

        $placeList = array();
        while ($row = $result->fetch_assoc()) {
            $place = array();
            $place['id'] = (int)$row['id'];
            $place['name'] = $row['name'];
            $place['details'] = $row['details'];
            $place['activity_details'] = $row['activity_details'];
            $place['activity_image'] = $row['activity_image'];
            $place['address'] = $row['address'];
            $place['phone'] = $row['phone'];
            $place['opening_time'] = $row['opening_time'];
            $place['latitude'] = floatval($row['latitude']);
            $place['longitude'] = floatval($row['longitude']);
            $place['image_list'] = array();

            $sql = "SELECT image_file_name FROM prachuap_place_image WHERE place_id = {$place['id']}";
            if ($galleryResult = $db->query($sql)) {
                while ($galleryRow = $galleryResult->fetch_assoc()) {
                    array_push($place['image_list'], $galleryRow['image_file_name']);
                }
                $galleryResult->close();
            } else {
                $response[KEY_ERROR_CODE] = ERROR_CODE_ERROR;
                $response[KEY_ERROR_MESSAGE] = 'เกิดข้อผิดพลาดในการอ่านข้อมูล (2)';
                $errMessage = $db->error;
                $response[KEY_ERROR_MESSAGE_MORE] = "$errMessage\nSQL: $sql";
                return;
            }

            $place['nearby_list'] = array();

            $sql = "SELECT * FROM prachuap_nearby WHERE place_id = {$place['id']}";
            if ($nearbyResult = $db->query($sql)) {
                while ($nearbyRow = $nearbyResult->fetch_assoc()) {
                    $nearby = array();
                    $nearby['id'] = (int)$nearbyRow['id'];
                    $nearby['name'] = $nearbyRow['name'];
                    $nearby['details'] = $nearbyRow['details'];
                    $nearby['cover_image'] = $nearbyRow['cover_image'];
                    $nearby['address'] = $nearbyRow['address'];
                    $nearby['phone'] = $nearbyRow['phone'];
                    $nearby['opening_time'] = $nearbyRow['opening_time'];
                    $nearby['type'] = $nearbyRow['type'];
                    $nearby['image_list'] = array();

                    $sql = "SELECT image_file_name FROM prachuap_nearby_image WHERE nearby_id = {$nearby['id']}";
                    if ($nearbyGalleryResult = $db->query($sql)) {
                        while ($nearbyGalleryRow = $nearbyGalleryResult->fetch_assoc()) {
                            array_push($nearby['image_list'], $nearbyGalleryRow['image_file_name']);
                        }
                        $nearbyGalleryResult->close();
                    } else {
                        $response[KEY_ERROR_CODE] = ERROR_CODE_ERROR;
                        $response[KEY_ERROR_MESSAGE] = 'เกิดข้อผิดพลาดในการอ่านข้อมูล (4)';
                        $errMessage = $db->error;
                        $response[KEY_ERROR_MESSAGE_MORE] = "$errMessage\nSQL: $sql";
                        return;
                    }

                    array_push($place['nearby_list'], $nearby);
                }
                $nearbyResult->close();
            } else {
                $response[KEY_ERROR_CODE] = ERROR_CODE_ERROR;
                $response[KEY_ERROR_MESSAGE] = 'เกิดข้อผิดพลาดในการอ่านข้อมูล (3)';
                $errMessage = $db->error;
                $response[KEY_ERROR_MESSAGE_MORE] = "$errMessage\nSQL: $sql";
                return;
            }

            array_push($placeList, $place);
        }
        $result->close();
        $response[KEY_DATA_LIST] = $placeList;
    } else {
        $response[KEY_ERROR_CODE] = ERROR_CODE_ERROR;
        $response[KEY_ERROR_MESSAGE] = 'เกิดข้อผิดพลาดในการอ่านข้อมูล (1)';
        $errMessage = $db->error;
        $response[KEY_ERROR_MESSAGE_MORE] = "$errMessage\nSQL: $sql";
    }
}

function doUpdatePlace()
{
    global $db, $response;

    $id = $db->real_escape_string($_POST['placeId']);
    $name = trim($db->real_escape_string($_POST['name']));
    $phone = trim($db->real_escape_string($_POST['phone']));
    $address = trim($db->real_escape_string($_POST['address']));
    $latitude = $db->real_escape_string($_POST['latitude']);
    $longitude = $db->real_escape_string($_POST['longitude']);
    $openingTime = $db->real_escape_string($_POST['openingTime']);
    $details = trim($db->real_escape_string($_POST['details']));
    $activityDetails = trim($db->real_escape_string($_POST['activityDetails']));

    $coverImageFileName = NULL;
    if ($_FILES['coverImageFile']['name'] !== '') {
        if (!moveUploadedFile('coverImageFile', DIR_IMAGES, $coverImageFileName)) {
            $response[KEY_ERROR_CODE] = ERROR_CODE_ERROR;
            $response[KEY_ERROR_MESSAGE] = 'เกิดข้อผิดพลาดในการอัพโหลดไฟล์ (รูปภาพหน้ากิจกรรม)';
            $response[KEY_ERROR_MESSAGE_MORE] = '';
            return;
        }
    }
    $setCoverFileName = $coverImageFileName ? "activity_image = '$coverImageFileName', " : '';

    $db->query('START TRANSACTION');

    $sql = "UPDATE prachuap_place 
                SET $setCoverFileName
                    name = '$name', details = '$details', phone = '$phone', 
                    address = '$address', latitude = $latitude, longitude = $longitude,
                    opening_time = '$openingTime', activity_details = '$activityDetails'
                WHERE id = $id";

    if ($result = $db->query($sql)) {
        for ($i = 0; $i < sizeof($_FILES[KEY_IMAGE_FILES]['name']); $i++) {
            if ($_FILES[KEY_IMAGE_FILES]['name'][$i] !== '') {
                $fileName = null;

                if (!moveUploadedFile(KEY_IMAGE_FILES, DIR_IMAGES, $fileName, $i)) {
                    $db->query('ROLLBACK');

                    $response[KEY_ERROR_CODE] = ERROR_CODE_ERROR;
                    $errorValue = $_FILES[KEY_IMAGE_FILES]['error'][$i];
                    $response[KEY_ERROR_MESSAGE] = "เกิดข้อผิดพลาดในการอัพโหลดรูปภาพ [Error: $errorValue]";
                    $response[KEY_ERROR_MESSAGE_MORE] = '';
                    return;
                }

                $sql = "INSERT INTO prachuap_place_image (place_id, image_file_name) 
                    VALUES ($id, '$fileName')";
                if (!($insertCourseAssetResult = $db->query($sql))) {
                    $db->query('ROLLBACK');

                    $response[KEY_ERROR_CODE] = ERROR_CODE_ERROR;
                    $response[KEY_ERROR_MESSAGE] = 'เกิดข้อผิดพลาดในการบันทึกข้อมูลรูปภาพ Gallery: ' . $db->error;
                    $response[KEY_ERROR_MESSAGE_MORE] = '';
                    return;
                }
            }
        }

        $response[KEY_ERROR_CODE] = ERROR_CODE_SUCCESS;
        $response[KEY_ERROR_MESSAGE] = 'แก้ไขข้อมูลสำเร็จ';
        $response[KEY_ERROR_MESSAGE_MORE] = '';

        $db->query('COMMIT');
    } else {
        $db->query('ROLLBACK');

        $response[KEY_ERROR_CODE] = ERROR_CODE_ERROR;
        $response[KEY_ERROR_MESSAGE] = 'เกิดข้อผิดพลาดในการแก้ไขข้อมูล: ' . $db->error;
        $errMessage = $db->error;
        $response[KEY_ERROR_MESSAGE_MORE] = "$errMessage\nSQL: $sql";
    }
}

function doDeletePlaceImage()
{
    global $db, $response;

    $assetId = $db->real_escape_string($_POST['assetId']);

    $sql = "DELETE FROM prachuap_place_image WHERE id = $assetId";
    if ($result = $db->query($sql)) {
        $response[KEY_ERROR_CODE] = ERROR_CODE_SUCCESS;
        $response[KEY_ERROR_MESSAGE] = 'ลบข้อมูลสำเร็จ';
        $response[KEY_ERROR_MESSAGE_MORE] = '';
    } else {
        $response[KEY_ERROR_CODE] = ERROR_CODE_ERROR;
        $response[KEY_ERROR_MESSAGE] = 'เกิดข้อผิดพลาดในการลบข้อมูล';
        $errMessage = $db->error;
        $response[KEY_ERROR_MESSAGE_MORE] = "$errMessage\nSQL: $sql";
    }
}

function doAddNearby()
{
    global $db, $response;

    $name = trim($db->real_escape_string($_POST['name']));
    $type = $db->real_escape_string($_POST['placeType']);
    $address = trim($db->real_escape_string($_POST['address']));
    $phone = trim($db->real_escape_string($_POST['phone']));
    $details = trim($db->real_escape_string($_POST['details']));
    $placeId = $db->real_escape_string($_POST['placeId']);

    if (!moveUploadedFile('coverImageFile', DIR_IMAGES, $coverImageFileName)) {
        $response[KEY_ERROR_CODE] = ERROR_CODE_ERROR;
        $response[KEY_ERROR_MESSAGE] = 'เกิดข้อผิดพลาดในการอัพโหลดไฟล์ (รูปภาพ Cover)';
        $response[KEY_ERROR_MESSAGE_MORE] = '';
        return;
    }

    $db->query('START TRANSACTION');

    $sql = "INSERT INTO prachuap_nearby (name, type, address, phone, details, cover_image, place_id) 
                VALUES ('$name', '$type', '$address', '$phone', '$details', '$coverImageFileName', $placeId)";
    if ($result = $db->query($sql)) {
        $insertId = $db->insert_id;

        for ($i = 0; $i < sizeof($_FILES[KEY_IMAGE_FILES]['name']); $i++) {
            if ($_FILES[KEY_IMAGE_FILES]['name'][$i] !== '') {
                $fileName = null;

                if (!moveUploadedFile(KEY_IMAGE_FILES, DIR_IMAGES, $fileName, $i)) {
                    $db->query('ROLLBACK');

                    $response[KEY_ERROR_CODE] = ERROR_CODE_ERROR;
                    $errorValue = $_FILES[KEY_IMAGE_FILES]['error'][$i];
                    $response[KEY_ERROR_MESSAGE] = "เกิดข้อผิดพลาดในการอัพโหลดรูปภาพ [Error: $errorValue]";
                    $response[KEY_ERROR_MESSAGE_MORE] = '';
                    return;
                }

                $sql = "INSERT INTO prachuap_nearby_image (nearby_id, image_file_name) 
                    VALUES ($insertId, '$fileName')";
                if (!($insertCourseAssetResult = $db->query($sql))) {
                    $db->query('ROLLBACK');

                    $response[KEY_ERROR_CODE] = ERROR_CODE_ERROR;
                    $response[KEY_ERROR_MESSAGE] = 'เกิดข้อผิดพลาดในการบันทึกข้อมูลรูปภาพ Gallery: ' . $db->error;
                    $response[KEY_ERROR_MESSAGE_MORE] = '';
                    return;
                }
            }
        }

        $response[KEY_ERROR_CODE] = ERROR_CODE_SUCCESS;
        $response[KEY_ERROR_MESSAGE] = 'เพิ่มข้อมูลสำเร็จ';
        $response[KEY_ERROR_MESSAGE_MORE] = '';

        $db->query('COMMIT');
    } else {
        $db->query('ROLLBACK');

        $response[KEY_ERROR_CODE] = ERROR_CODE_ERROR;
        $response[KEY_ERROR_MESSAGE] = 'เกิดข้อผิดพลาดในการเพิ่มข้อมูล: ' . $db->error;
        $errMessage = $db->error;
        $response[KEY_ERROR_MESSAGE_MORE] = "$errMessage\nSQL: $sql";
    }
}

function doUpdateNearby()
{
    global $db, $response;

    $id = $db->real_escape_string($_POST['id']);
    $name = trim($db->real_escape_string($_POST['name']));
    $address = trim($db->real_escape_string($_POST['address']));
    $phone = trim($db->real_escape_string($_POST['phone']));
    $details = trim($db->real_escape_string($_POST['details']));
    $placeId = $db->real_escape_string($_POST['placeId']);

    $coverImageFileName = NULL;
    if ($_FILES['coverImageFile']['name'] !== '') {
        if (!moveUploadedFile('coverImageFile', DIR_IMAGES, $coverImageFileName)) {
            $response[KEY_ERROR_CODE] = ERROR_CODE_ERROR;
            $response[KEY_ERROR_MESSAGE] = 'เกิดข้อผิดพลาดในการอัพโหลดไฟล์ (รูปภาพ Cover)';
            $response[KEY_ERROR_MESSAGE_MORE] = '';
            return;
        }
    }
    $setCoverFileName = $coverImageFileName ? "cover_image = '$coverImageFileName', " : '';

    $db->query('START TRANSACTION');

    $sql = "UPDATE prachuap_nearby 
                SET $setCoverFileName 
                    name = '$name', address = '$address', phone = '$phone', 
                    details = '$details', place_id = $placeId
                WHERE id = $id";

    if ($result = $db->query($sql)) {
        for ($i = 0; $i < sizeof($_FILES[KEY_IMAGE_FILES]['name']); $i++) {
            if ($_FILES[KEY_IMAGE_FILES]['name'][$i] !== '') {
                $fileName = null;

                if (!moveUploadedFile(KEY_IMAGE_FILES, DIR_IMAGES, $fileName, $i)) {
                    $db->query('ROLLBACK');

                    $response[KEY_ERROR_CODE] = ERROR_CODE_ERROR;
                    $errorValue = $_FILES[KEY_IMAGE_FILES]['error'][$i];
                    $response[KEY_ERROR_MESSAGE] = "เกิดข้อผิดพลาดในการอัพโหลดรูปภาพ [Error: $errorValue]";
                    $response[KEY_ERROR_MESSAGE_MORE] = '';
                    return;
                }

                $sql = "INSERT INTO prachuap_nearby_image (nearby_id, image_file_name)
                    VALUES ($id, '$fileName')";
                if (!($insertCourseAssetResult = $db->query($sql))) {
                    $db->query('ROLLBACK');

                    $response[KEY_ERROR_CODE] = ERROR_CODE_ERROR;
                    $response[KEY_ERROR_MESSAGE] = 'เกิดข้อผิดพลาดในการบันทึกข้อมูลรูปภาพ Gallery: ' . $db->error;
                    $response[KEY_ERROR_MESSAGE_MORE] = '';
                    return;
                }
            }
        }

        $response[KEY_ERROR_CODE] = ERROR_CODE_SUCCESS;
        $response[KEY_ERROR_MESSAGE] = 'แก้ไขข้อมูลสำเร็จ';
        $response[KEY_ERROR_MESSAGE_MORE] = '';

        $db->query('COMMIT');
    } else {
        $db->query('ROLLBACK');

        $response[KEY_ERROR_CODE] = ERROR_CODE_ERROR;
        $response[KEY_ERROR_MESSAGE] = 'เกิดข้อผิดพลาดในการแก้ไขข้อมูล: ' . $db->error;
        $errMessage = $db->error;
        $response[KEY_ERROR_MESSAGE_MORE] = "$errMessage\nSQL: $sql";
    }
}

function doDeleteNearby()
{
    global $db, $response;

    $id = $db->real_escape_string($_POST['id']);

    $deleteNewsSql = "DELETE FROM prachuap_nearby WHERE id = $id";

    if ($deleteResult = $db->query($deleteNewsSql)) {
        $deletePlaceAssetsSql = "DELETE FROM prachuap_nearby_image WHERE nearby_id = $id";

        if ($deletePlaceAssetsResult = $db->query($deletePlaceAssetsSql)) {
            $response[KEY_ERROR_CODE] = ERROR_CODE_SUCCESS;
            $response[KEY_ERROR_MESSAGE] = 'ลบข้อมูลสำเร็จ';
            $response[KEY_ERROR_MESSAGE_MORE] = '';
        } else {
            $response[KEY_ERROR_CODE] = ERROR_CODE_ERROR;
            $response[KEY_ERROR_MESSAGE] = 'เกิดข้อผิดพลาดในการลบข้อมูล (2): ' . $db->error;
            $errMessage = $db->error;
            $response[KEY_ERROR_MESSAGE_MORE] = "$errMessage\nSQL: $deletePlaceAssetsSql";
        }
    } else {
        $response[KEY_ERROR_CODE] = ERROR_CODE_ERROR;
        $response[KEY_ERROR_MESSAGE] = 'เกิดข้อผิดพลาดในการลบข้อมูล (1): ' . $db->error;
        $errMessage = $db->error;
        $response[KEY_ERROR_MESSAGE_MORE] = "$errMessage\nSQL: $deleteNewsSql";
    }
}

function doDeleteNearbyImage()
{
    global $db, $response;

    $assetId = $db->real_escape_string($_POST['assetId']);

    $sql = "DELETE FROM prachuap_nearby_image WHERE id = $assetId";
    if ($result = $db->query($sql)) {
        $response[KEY_ERROR_CODE] = ERROR_CODE_SUCCESS;
        $response[KEY_ERROR_MESSAGE] = 'ลบข้อมูลสำเร็จ';
        $response[KEY_ERROR_MESSAGE_MORE] = '';
    } else {
        $response[KEY_ERROR_CODE] = ERROR_CODE_ERROR;
        $response[KEY_ERROR_MESSAGE] = 'เกิดข้อผิดพลาดในการลบข้อมูล';
        $errMessage = $db->error;
        $response[KEY_ERROR_MESSAGE_MORE] = "$errMessage\nSQL: $sql";
    }
}


function createRandomString($length)
{
    $key = '';
    $keys = array_merge(range(0, 9), range('a', 'z'));

    for ($i = 0; $i < $length; $i++) {
        $key .= $keys[array_rand($keys)];
    }

    return $key;
}

function moveUploadedFile($key, $dest, &$randomFileName, $index = -1)
{
    global $response;

    $clientName = $index === -1 ? $_FILES[$key]['name'] : $_FILES[$key]['name'][$index];
    $response['name'] = $clientName;
    $response['type'] = $index === -1 ? $_FILES[$key]['type'] : $_FILES[$key]['type'][$index];
    $response['size'] = $index === -1 ? $_FILES[$key]['size'] : $_FILES[$key]['size'][$index];
    $response['tmp_name'] = $index === -1 ? $_FILES[$key]['tmp_name'] : $_FILES[$key]['tmp_name'][$index];

    $src = $index === -1 ? $_FILES[$key]['tmp_name'] : $_FILES[$key]['tmp_name'][$index];
    $response['upload_src'] = $src;
    $response['upload_dest'] = $dest;

    //$date = date('Y-m-d H:i:s');
    //$timestamp = time();
    $timestamp = round(microtime(true) * 1000);
    $randomFileName = "{$timestamp}-{$clientName}";
    return move_uploaded_file($src, "{$dest}{$randomFileName}");
}

function moveUploadedFile_Old($key, $dest)
{
    global $response;

    $response['name'] = $_FILES[$key]['name'];
    $response['type'] = $_FILES[$key]['type'];
    $response['size'] = $_FILES[$key]['size'];
    $response['tmp_name'] = $_FILES[$key]['tmp_name'];

    $src = $_FILES[$key]['tmp_name'];
    $response['upload_src'] = $src;

    $response['upload_dest'] = $dest;

    return move_uploaded_file($src, $dest);
}

function getUploadErrorMessage($errCode)
{
    $message = '';
    switch ($errCode) {
        case UPLOAD_ERR_OK:
            break;
        case UPLOAD_ERR_INI_SIZE:
        case UPLOAD_ERR_FORM_SIZE:
            $message .= 'File too large (limit of ' . get_max_upload() . ' bytes).';
            break;
        case UPLOAD_ERR_PARTIAL:
            $message .= 'File upload was not completed.';
            break;
        case UPLOAD_ERR_NO_FILE:
            $message .= 'Zero-length file uploaded.';
            break;
        default:
            $message .= 'Internal error #' . $errCode;
            break;
    }
    return $message;
}

?>
