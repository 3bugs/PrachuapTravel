<?php
require_once '../include/head_php.inc';

$placeType = $_GET['place_type'];
$placeTypeList = array(
    'restaurant', 'hotel'
);
if (!isset($placeType) || !in_array($placeType, $placeTypeList)) {
    echo "Invalid place type '$placeType' - ระบุประเภทสถานที่ไม่ถูกต้อง";
    $db->close();
    exit();
}

$pageTitles['restaurant'] = 'ร้านอาหาร';
$pageTitles['hotel'] = 'ที่พัก';

$placeTypeKeys['restaurant'] = 'restaurant';
$placeTypeKeys['hotel'] = 'hotel';

$pageTitle = $pageTitles[$placeType];
$placeTypeKey = $placeTypeKeys[$placeType];

$sql = "SELECT n.id, n.name, n.details, n.cover_image, n.address, n.phone, p.name AS place_name
            FROM prachuap_nearby n 
                INNER JOIN prachuap_place p 
                    ON p.id = n.place_id
            WHERE type = '$placeTypeKey'
            ORDER BY id";
if ($result = $db->query($sql)) {
    $placeList = array();
    while ($row = $result->fetch_assoc()) {
        array_push($placeList, $row);
    }
    $result->close();
} else {
    echo 'เกิดข้อผิดพลาดในการเชื่อมต่อฐานข้อมูล: ' . $db->error;
    $db->close();
    exit();
}

$userHasPermission = true;

?>
    <!DOCTYPE html>
    <html lang="th">
    <head>
        <?php require_once('../include/head.inc'); ?>
        <!-- DataTables -->
        <link rel="stylesheet" href="../bower_components/datatables.net-bs/css/dataTables.bootstrap.min.css">
        <!--Lightbox-->
        <link href="../dist/lightbox/css/lightbox.css" rel="stylesheet">

        <style>
            #tableDownload td:nth-child(5) {
                text-align: center;
            }
        </style>
    </head>
    <body class="hold-transition skin-purple sidebar-mini fixed">

    <div class="wrapper">
        <?php require_once('../include/header.inc'); ?>
        <?php require_once('../include/sidebar.inc'); ?>

        <!-- Content Wrapper. Contains page content -->
        <div class="content-wrapper">
            <!-- Content Header (Page header) -->
            <section class="content-header">
                <h1>
                    <?= trim($pageTitle); ?>
                </h1>
            </section>

            <!-- Main content -->
            <section class="content">
                <div class="row">
                    <div class="col-xs-12">
                        <div class="box">
                            <div class="box-header">
                                <h3 class="box-title">&nbsp;</h3>
                                <?php
                                if ($userHasPermission) {
                                    ?>
                                    <button type="button" class="btn btn-success pull-right"
                                            onclick="onClickAdd(this)">
                                        <span class="fa fa-plus"></span>&nbsp;
                                        เพิ่ม<?= $pageTitle; ?>
                                    </button>
                                    <?php
                                }
                                ?>
                            </div>
                            <div class="box-body">
                                <table id="tablePlace_<?= $placeType; ?>" class="table table-bordered table-striped">
                                    <thead>
                                    <tr>
                                        <th style="text-align: center; width: 15%;">ชื่อ<?= $pageTitle; ?></th>
                                        <th style="text-align: center; width: 40%;">รายละเอียด</th>
                                        <th style="text-align: center; width: 30%;">ที่อยู่</th>
                                        <th style="text-align: center; width: 15%;">เบอร์โทร</th>
                                        <th style="text-align: center;">รูปภาพ</th>
                                        <th style="text-align: center;">จัดการ</th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    <?php
                                    if (sizeof($placeList) == 0) {
                                        ?>
                                        <!--<tr valign="middle">
                                            <td colspan="20" align="center">ไม่มีข้อมูล</td>
                                        </tr>-->
                                        <?php
                                    } else {
                                        foreach ($placeList as $place) {
                                            ?>
                                            <tr style="">
                                                <!--ชื่อสถานที่-->
                                                <td><strong><?= $place['name']; ?></strong><br><div style="margin-top: 5px"><small>(ใกล้<?= $place['place_name']; ?>)</small></div></td>

                                                <!--รายละเอียด-->
                                                <td <?= $place['details'] ? '' : 'style="text-align: center"'; ?>><?= $place['details'] ? $place['details'] : '<span style="color: orangered">ไม่มีข้อมูล</span>'; ?></td>

                                                <!--ที่อยู่-->
                                                <td <?= $place['address'] ? '' : 'style="text-align: center"'; ?>><?= $place['address'] ? $place['address'] : '<span style="color: orangered">ไม่มีข้อมูล</span>'; ?></td>

                                                <!--เบอร์โทร-->
                                                <td style="text-align: center"><?= $place['phone'] ? $place['phone'] : '<span style="color: orangered">ไม่มีข้อมูล</span>'; ?></td>

                                                <!--ภาพ Cover-->
                                                <td style="text-align: center; cursor: pointer">
                                                    <a href="<?= (DIR_IMAGES . ($place['cover_image'] ? $place['cover_image'] : 'ic_no_image_2.png')); ?>"
                                                       data-lightbox="coverImage" data-title="รูปภาพ '<?= $place['name']; ?>'">
                                                        <img src="<?= (DIR_IMAGES . ($place['cover_image'] ? $place['cover_image'] : 'ic_no_image_2.png')); ?>"
                                                             height="60px">
                                                    </a>
                                                </td>

                                                <td nowrap>
                                                    <form method="get" action="nearby_add_edit.php" style="display: inline; margin: 0">
                                                        <input type="hidden" name="place_type" value="<?= $placeType; ?>"/>
                                                        <input type="hidden" name="place_id" value="<?= $place['id']; ?>"/>

                                                        <?php
                                                        if ($userHasPermission) {
                                                            ?>
                                                            <button type="submit" class="btn btn-warning"
                                                                    style="margin-left: 3px">
                                                                <span class="fa fa-pencil"></span>&nbsp;
                                                                แก้ไข
                                                            </button>
                                                            <button type="button" class="btn btn-danger"
                                                                    style="margin-left: 3px; margin-right: 3px"
                                                                    onclick="onClickDelete(this, <?= $place['id']; ?>, '<?= $place['name']; ?>')">
                                                                <span class="fa fa-remove"></span>&nbsp;
                                                                ลบ
                                                            </button>
                                                            <?php
                                                        }
                                                        ?>
                                                    </form>
                                                </td>
                                            </tr>
                                            <?php
                                        }
                                    }
                                    ?>
                                    </tbody>
                                </table>
                            </div>
                            <!-- /.box-body -->
                        </div>
                        <!-- /.box -->
                    </div>
                    <!-- /.col -->
                </div>
                <!-- /.row -->
            </section>
            <!-- /.content -->
        </div>
        <!-- /.content-wrapper -->

        <?php require_once('../include/footer.inc'); ?>
    </div>
    <!-- ./wrapper -->

    <script>
        let downloadListDataTable = null;

        $(document).ready(function () {
            $('#tablePlace_<?= $placeType; ?>').DataTable({
                stateSave: true,
                stateDuration: -1, // sessionStorage
                order: [[3, 'desc']],
                language: {
                    lengthMenu: "แสดงหน้าละ _MENU_ แถวข้อมูล",
                    zeroRecords: "ไม่มีข้อมูล",
                    emptyTable: "ไม่มีข้อมูล",
                    info: "หน้าที่ _PAGE_ จากทั้งหมด _PAGES_ หน้า",
                    infoEmpty: "แสดง 0 แถวข้อมูล",
                    infoFiltered: "(กรองจากทั้งหมด _MAX_ แถวข้อมูล)",
                    search: "ค้นหา:",
                    thousands: ",",
                    loadingRecords: "รอสักครู่...",
                    processing: "กำลังประมวลผล...",
                    paginate: {
                        first: "หน้าแรก",
                        last: "หน้าสุดท้าย",
                        next: "ถัดไป",
                        previous: "ก่อนหน้า"
                    },
                },
                drawCallback: function (row, data) {
                    $('.my-toggle').bootstrapToggle();
                }
            });

            lightbox.option({
                fadeDuration: 500,
                imageFadeDuration: 500,
                resizeDuration: 500,
            });
        });

        function onClickAdd() {
            window.location.href = 'nearby_add_edit.php?place_type=<?= $placeType; ?>';
        }

        function onChangePlaceRecommend(element, placeId) {
            let result = confirm("ยืนยัน" + (element.checked ? 'ตั้ง' : 'ยกเลิก') + "เป็น<?= $placeType === 'otop' ? 'สินค้า' : 'สถานที่' ?>แนะนำ?");
            ;
            if (result) {
                doChangePlaceRecommend(placeId, (element.checked ? 1 : 0));
            } else {
                /*รีโหลด เพื่อให้สถานะ checkbox กลับมาเหมือนเดิม*/
                location.reload(true);
            }
        }

        function onClickDelete(element, id, title) {
            BootstrapDialog.show({
                title: 'ลบ<?= $pageTitle; ?>',
                message: 'ยืนยันลบ \'' + title + '\' ?',
                buttons: [{
                    label: 'ลบ',
                    action: function (self) {
                        doDeleteplace(id);
                        self.close();
                    },
                    cssClass: 'btn-primary'
                }, {
                    label: 'ยกเลิก',
                    action: function (self) {
                        self.close();
                    }
                }]
            });
        }

        function doDeleteplace(id) {
            $.post(
                '../api/api.php/delete_nearby',
                {
                    id: id,
                }
            ).done(function (data) {
                if (data.error_code === 0) {
                    location.reload(true);
                } else {
                    BootstrapDialog.show({
                        title: 'ลบ<?= $pageTitle; ?> - ผิดพลาด',
                        message: data.error_message,
                        buttons: [{
                            label: 'ปิด',
                            action: function (self) {
                                self.close();
                            }
                        }]
                    });
                }
            }).fail(function () {
                BootstrapDialog.show({
                    title: 'ลบ<?= $pageTitle; ?> - ผิดพลาด',
                    message: 'เกิดข้อผิดพลาดในการเชื่อมต่อ Server',
                    buttons: [{
                        label: 'ปิด',
                        action: function (self) {
                            self.close();
                        }
                    }]
                });
            });
        }
    </script>

    <?php require_once('../include/foot.inc'); ?>
    <!-- DataTables -->
    <script src="../bower_components/datatables.net/js/jquery.dataTables.min.js"></script>
    <script src="../bower_components/datatables.net-bs/js/dataTables.bootstrap.min.js"></script>
    <!--Lightbox-->
    <script src="../dist/lightbox/js/lightbox.js"></script>

    </body>
    </html>

<?php
require_once '../include/foot_php.inc';
?>