# 网上商城

## 登录购物演示

![](./img/buy.gif)

## 支付演示

![](./img/pay.gif)

# 服务器情况

**服务器类型**：阿里云轻量应用服务器

**CPU核心数**：单核

**内存**：2G

**磁盘**：40G

# 支付功能

支付模块是本项目的亮点和难点，本项目对接了支付宝的'当面付'，即'扫码支付'，通过订单号生成付款二维码，上传FTP服务器，展示给客户扫码成功付款后（通过支付宝沙箱环境模拟支付），收到支付宝回调，商家验证签名正确性，然后进行后续操作

具体可查看[支付宝官方文档](https://doc.open.alipay.com/docs/doc.htm?spm=a219a.7629140.0.0.Q4tRmQ&treeId=193&articleId=105072&docType=1)<br>
以及[支付宝沙箱](https://openhome.alipay.com/platform/appDaily.htm?tab=info)<br>

## 业务流程

![](http://img01.taobaocdn.com/top/i1/LB1KXhmLXXXXXaIapXXXXXXXXXX)

## 调用流程

![流程图](https://img.alicdn.com/top/i1/LB14VRALXXXXXcnXXXXXXXXXXXX)

1. 商户系统调用支付宝预下单接口`alipay.trade.precreate`，获得该订单二维码图片地址。

2. 发起轮询获得支付结果：等待`5`秒后调用交易查询接口`alipay.trade.query`通过支付时传入的商户订单号(`out_trade_no`)查询支付结果（返回参数`TRADE_STATUS`），如果仍然返回等待用户付款（`WAIT_BUYER_PAY`），则再次等待5秒后继续查询，直到返回确切的支付结果（成功`TRADE_SUCCESS` 或 已撤销关闭`TRADE_CLOSED`），或是超出轮询时间。在最后一次查询仍然返回等待用户付款的情况下，必须立即调用交易撤销接口`alipay.trade.cancel`将这笔交易撤销，避免用户继续支付。

3. 除了主动轮询，也可以通过接受异步通知获得支付结果，详见扫码异步通知，注意一定要对异步通知做验签，确保通知是支付宝发出的。