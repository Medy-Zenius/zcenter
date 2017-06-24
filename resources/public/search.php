<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <!-- The above 3 meta tags *must* come first in the head; any other head content must come *after* these tags -->
    <meta name="format-detection" content="telephone=no"/>
    <link rel="icon" href="images/favicon.ico" type="image/x-icon">
    <title>Search</title>

    <!-- Bootstrap -->
    <link href="css/bootstrap.css" rel="stylesheet">

    <!-- Links -->

    <link rel="stylesheet" href="css/search.css">


    <!--JS-->
    <script src="js/jquery.js"></script>
    <script src="js/jquery-migrate-1.2.1.min.js"></script>

    <!--[if lt IE 9]>
    <div style=' clear: both; text-align:center; position: relative;'>
        <a href="http://windows.microsoft.com/en-US/internet-explorer/..">
            <img src="images/ie8-panel/warning_bar_0000_us.jpg" border="0" height="42" width="820"
                 alt="You are using an outdated browser. For a faster, safer browsing experience, upgrade for free today."/>
        </a>
    </div>
    <script src="js/html5shiv.js"></script>
    <![endif]-->
    <script src='js/device.min.js'></script>
</head>
<body>
<div class="page text-center">
    <!--========================================================
                              HEADER
    =========================================================-->

    <header class="bg-primary-variant-1">
        <div id="stuck_container" class="stuck_container">
            <section class="bg-white">
                <div class="container">

                    <!-- Navbar -->
                    <nav class="navbar navbar-static-top text-left">
                        <ul class="navbar-nav sf-menu" data-type="navbar">
                            <li>
                                <a href="./">Home</a>
                            </li>
                            <li class="dropdown">
                                <a href="index-1.html">About us</a>
                                <ul class="dropdown-menu">
                                    <li>
                                        <a href="#">Overview</a>
                                    </li>
                                    <li>
                                        <a href="#">Publication</a>
                                        <ul class="dropdown-menu">
                                            <li>
                                                <a href="#">Latest</a>
                                            </li>
                                            <li>
                                                <a href="#">Popular</a>
                                            </li>
                                            <li>
                                                <a href="#">Archive</a>
                                            </li>
                                        </ul>
                                    </li>
                                    <li>
                                        <a href="#">History</a>
                                    </li>
                                </ul>
                            </li>
                            <li>
                                <a href="index-2.html">Books</a>
                            </li>
                            <li>
                                <a href="index-3.html">Authors</a>
                            </li>
                            <li>
                                <a href="index-4.html">News</a>
                            </li>
                            <li>
                                <a href="index-5.html">Contact</a>
                            </li>
                        </ul>
                    </nav>
                    <!-- END Navbar -->

                </div>
            </section>

            <section class="skew skew__deg-1 skew__deg-1-tm skew__deg-1-pt-0">
                <div class="skew_sub-1 skew_sub-1-2">
                    <div class="skew_sub-2 skew_sub-2-2 skew_sub-2-tm">
                        <div class="skew_bg bg-primary skew_bg-2"></div>
                    </div>
                </div>
                <div class="container">

                    <!-- Navbar Brand -->
                    <div class="navbar-header">
                        <h1 class="navbar-brand">
                            <a href="./">
                                <small>Publishing Company</small>
                                Booking
                            </a>
                        </h1>
                    </div>
                    <!-- END Navbar Brand -->

                </div>
            </section>

        </div>
    </header>

    <!--========================================================
                              CONTENT
    =========================================================-->
    <main>
        <section id="content" class="content skew skew__deg-2 skew__deg-2-m-8 skew__deg-3_before-inset">
            <div class="skew_sub-1 skew_sub-1-m-8">
                <div class="skew_sub-2">
                    <div class="skew_bg bg-white"></div>
                </div>
            </div>
            <div class="skew_cnt container contain-wrap well-5 well-5--inset-2 well-5--inset-3">
                <div class="search-form">
                    <form id="search" action="search.php" method="GET" accept-charset="utf-8">
                        <label class="search-form_label" for="in">
                            <input id="in" class="search-form_input" type="text" name="s"
                                   placeholder="Search"/>
                            <span class="search-form_liveout"></span>
                        </label>
                        <button type="submit" class="search-form_submit"></button>
                    </form>
                </div>
                <h3>Search Results</h3>

                <div id="search-results"></div>
            </div>
        </section>
    </main>

    <!--========================================================
                              FOOTER
    =========================================================-->
    <footer>

        <footer>
            <div class="contain-wrap">
                <div class="container">
                    <p class="rights">Book Publishing Company &#169; <span id="copyright-year"></span> All rights
                        reserved.<br>
                        Terms of use &#124; <a href="index-6.html">Privacy Policy</a>
                    </p>
                </div>
            </div>
        </footer>

    </footer>
</div>

<!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
<!-- Include all compiled plugins (below), or include individual files as needed -->
<script src="js/bootstrap.min.js"></script>
<script src="js/tm-scripts.js"></script>
<!-- </script> -->


</body>
</html>
