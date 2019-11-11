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
            $place['address'] = $row['address'];
            $place['phone'] = $row['phone'];
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

function createRandomString($length)
{
    $key = '';
    $keys = array_merge(range(0, 9), range('a', 'z'));

    for ($i = 0; $i < $length; $i++) {
        $key .= $keys[array_rand($keys)];
    }

    return $key;
}

function moveUploadedFile($key, $dest)
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
