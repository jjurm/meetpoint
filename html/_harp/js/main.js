$(document).ready(function() {
    console.log("hey");
    // bind 'myForm' and provide a simple callback function
    $("#myForm").ajaxForm(function() {
        alert("Thank you!");
    });

    $('input[type=range]').on("change mousemove", function() {
        $(this).next().html($(this).val());
    });
});
